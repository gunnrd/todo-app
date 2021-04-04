package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var matcher: Matcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(registerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference

        buttonRegister.setOnClickListener {
            if (inputCheck()) {
                registerNewUser()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun registerNewUser() {
        auth.createUserWithEmailAndPassword(inputNewEmail.text.toString(), inputNewPassword.text.toString())
            .addOnCompleteListener(this) { register ->

                if (register.isSuccessful) {
                    verifyEmail()
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun verifyEmail() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) { sendVerification ->
            if (sendVerification.isSuccessful) {
                Toast.makeText(this, "Verification email sent to " + user.email, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputCheck(): Boolean {
        val check: Boolean

        when {
            inputNewEmail.text.toString().isEmpty() -> {
                Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
                check = false
            }
            !validateEmailFormat(inputNewEmail.text.toString()) -> {
                Toast.makeText(this, "Email format is incorrect", Toast.LENGTH_LONG).show()
                check = false
            }
            inputNewPassword.text.toString().isEmpty() || inputConfirmPassword.text.toString().isEmpty() -> {
                Toast.makeText(this, "Both password fields are required", Toast.LENGTH_LONG).show()
                check = false
            }
            inputNewPassword.text.toString() != inputConfirmPassword.text.toString() -> {
                Toast.makeText(this, "Passwords does not match", Toast.LENGTH_LONG).show()
                check = false
            }
            inputNewPassword.text.toString().length < 6 -> {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                check = false
            }

            !validatePasswordFormat(inputNewPassword.text.toString()) -> {
                Toast.makeText(this, "Password has invalid characters", Toast.LENGTH_LONG).show()
                check = false
            }
            else -> {
                check = true
            }
        }

        return check
    }

    private fun validateEmailFormat(email: String): Boolean {
        return(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    private fun validatePasswordFormat(password: String): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=\\S+$).{6,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)

        return matcher.matches()
    }
}