package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_my_profile.inputNewPassword
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Matcher
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

        buttonDisableHandler()

        buttonValidateUser.setOnClickListener {
            validateUser()
            buttonEnableHandler()

            /*
            if (validateUser()) {
                buttonEnableHandler()
            }*/
        }

        buttonSubmitNewPassword.setOnClickListener {
            if (inputCheckPassword()) {
                submitNewPassword()
            }
        }

        buttonSubmitNewEmail.setOnClickListener {
            submitNewEmailAddress()
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

    //TODO fix boolean expression
    private fun validateUser(): Boolean {
        var validation = false
        val currentUser = auth.currentUser
        val password = inputCurrentPassword.text.toString()

        currentUser?.let { user ->
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).addOnCompleteListener { update ->

                if (update.isSuccessful) {
                    Toast.makeText(this, "Account validated", Toast.LENGTH_SHORT).show()
                    inputCurrentPassword.text.clear()
                    validation = true
                }
                /*
                else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    false
                }*/
            }
        }

        return validation
    }

    private fun submitNewPassword() {
        val password = findViewById<EditText>(R.id.inputNewPassword).text.toString()

        //TODO fail check input fields
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

    private fun submitNewEmailAddress() {
        val email = findViewById<EditText>(R.id.inputChangeEmail).text.toString()

        //TODO fail check input fields
        auth.currentUser?.let { updateEmail ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Update email")
            alert.setMessage("Do you want to change email address?")

            alert.setPositiveButton("Ok") { _, _ ->
                updateEmail.updateEmail(email).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Toast.makeText(this, "Email updated", Toast.LENGTH_LONG).show()
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

    private fun deleteAccount() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete account")
        alert.setMessage("Warning! This will delete your account permanently!")

        alert.setPositiveButton("Delete") { _, _ ->
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

    //TODO fix
    private fun inputCheckEmail(): Boolean {
        var check: Boolean = false

        check = if (inputChangeEmail.text.toString().isEmpty()) {
            Toast.makeText(this, "New email address is required", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }

        return check
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
            //TODO check if 100% correct
            !validatePassword(inputNewPassword.text.toString()) -> {
                Toast.makeText(this, "Password has invalid characters", Toast.LENGTH_LONG).show()
                check = false
            }
            else -> {
                check = true
            }
        }

        return check
    }

    private fun validatePassword(password: String): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)

        return !matcher.matches()
    }

    private fun buttonEnableHandler() {
        buttonSubmitNewPassword.isEnabled = true
        buttonSubmitNewEmail.isEnabled = true
        buttonDeleteUser.isEnabled = true
    }

    private fun buttonDisableHandler() {
        buttonSubmitNewPassword.isEnabled = false
        buttonSubmitNewEmail.isEnabled = false
        buttonDeleteUser.isEnabled = false
    }
}