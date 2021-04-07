package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_my_profile.inputNewPassword
import java.util.regex.Pattern

class MyProfileActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setSupportActionBar(myProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        disableHandler()

        buttonValidateUser.setOnClickListener {
            validateUser()
        }

        buttonSubmitNewPassword.setOnClickListener {
            if (inputCheckPassword()) {
                submitNewPassword()
            }
        }

        buttonSubmitNewEmail.setOnClickListener {
            if (inputCheckEmail()) {
                submitNewEmailAddress()
            }
        }

        buttonDeleteUser.setOnClickListener {
            deleteAccount()
        }

        buttonLogOut.setOnClickListener {
            logout()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun validateUser() {
        val currentUser = auth.currentUser
        val password = inputCurrentPassword.text.toString()

        currentUser?.let { user ->
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            user.reauthenticate(credential).addOnCompleteListener { update ->

                if (update.isSuccessful) {
                    val textChangeOnAuthentication = "Account successfully authenticated"
                    textValidateAccountInfo.text = textChangeOnAuthentication
                    inputCurrentPassword.isVisible = false
                    buttonValidateUser.isVisible = false
                    enableHandler()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitNewPassword() {
        val password = findViewById<EditText>(R.id.inputNewPassword).text.toString()

        auth.currentUser?.let { updatePassword ->
            val alert = AlertDialog.Builder(this)

            alert.setTitle("Update password")
            alert.setMessage("Do you want to change password?")

            alert.setPositiveButton("Ok") { _, _ ->
                updatePassword.updatePassword(password).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_LONG).show()
                        inputNewPassword.text.clear()
                        inputConfirmNewPassword.text.clear()
                    } else {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_LONG).show()
                    }
                }
            }

            alert.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            alert.show()
        }
    }

    private fun inputCheckPassword(): Boolean {
        val check: Boolean

        when {
            inputNewPassword.text.toString().isEmpty() || inputConfirmNewPassword.text.toString().isEmpty() -> {
                Toast.makeText(this, "Both password fields are required", Toast.LENGTH_LONG).show()
                check = false
            }
            inputNewPassword.text.toString() != inputConfirmNewPassword.text.toString() -> {
                Toast.makeText(this, "Passwords does not match", Toast.LENGTH_LONG).show()
                check = false
            }
            inputNewPassword.text.toString().length < 6 -> {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                check = false
            }
            !validatePasswordFormat(inputNewPassword.text.toString()) -> {
                Toast.makeText(this, "Password has invalid characters. Whitespace is not allowed.", Toast.LENGTH_LONG).show()
                check = false
            }
            else -> {
                check = true
            }
        }

        return check
    }

    private fun validatePasswordFormat(password: String): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=\\S+$).{6,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)

        return matcher.matches()
    }

    private fun submitNewEmailAddress() {
        val email = findViewById<EditText>(R.id.inputChangeEmail).text.toString()

        auth.currentUser?.let { updateEmailAddress ->
            val alert = AlertDialog.Builder(this)
            
            alert.setTitle("Update email address")
            alert.setMessage("Do you want to change email address? If changed you will be logged out.")

            alert.setPositiveButton("Ok") { _, _ ->
                updateEmailAddress.updateEmail(email).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Toast.makeText(this, "Email address updated", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Failed to update email", Toast.LENGTH_LONG).show()
                    }
                }
            }

            alert.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            alert.show()
        }
    }

    private fun inputCheckEmail(): Boolean {
        return when {
            inputChangeEmail.text.toString().isEmpty() -> {
                Toast.makeText(this, "New email address is required", Toast.LENGTH_LONG).show()
                false
            }
            !validateEmailFormat(inputChangeEmail.text.toString()) -> {
                Toast.makeText(this, "Email format is incorrect", Toast.LENGTH_LONG).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        return(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    private fun deleteAccount() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete account")
        alert.setMessage("Warning! This will delete your account permanently.")

        alert.setPositiveButton("Delete") { _, _ ->
            val reference: DatabaseReference
            val database = FirebaseDatabase.getInstance().reference
            reference = database.child(auth.currentUser!!.uid)
            reference.removeValue()

            auth.currentUser!!.delete().addOnCompleteListener { delete ->
                if (delete.isSuccessful) {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun logout() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Log out")
        alert.setMessage("Do you want to log out?")

        alert.setPositiveButton("Ok") { _, _ ->
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun enableHandler() {
        buttonSubmitNewPassword.isEnabled = true
        buttonSubmitNewEmail.isEnabled = true
        buttonDeleteUser.isEnabled = true
        inputNewPassword.isEnabled = true
        inputConfirmNewPassword.isEnabled = true
        inputChangeEmail.isEnabled = true
    }

    private fun disableHandler() {
        buttonSubmitNewPassword.isEnabled = false
        buttonSubmitNewEmail.isEnabled = false
        buttonDeleteUser.isEnabled = false
        inputNewPassword.isEnabled = false
        inputConfirmNewPassword.isEnabled = false
        inputChangeEmail.isEnabled = false
    }
}