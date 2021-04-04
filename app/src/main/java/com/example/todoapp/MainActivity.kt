package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (auth.currentUser != null) {
            startActivity(Intent(this, TaskListActivity::class.java))
        }

        buttonLoginMain.setOnClickListener {
            if (inputCheck()) {
                logInUser()
            }
        }

        buttonRegisterNewUser.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        buttonForgotPasswordMain.setOnClickListener {
            resetPassword()
        }
    }

    private fun logInUser() {
        auth.signInWithEmailAndPassword(inputEmail.text.toString(), inputPassword.text.toString())
            .addOnCompleteListener(this) { login ->

            if (login.isSuccessful) {
                startActivity(Intent(this, TaskListActivity::class.java))
            } else {
                Toast.makeText(this, "Login failed", LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword() {
        val alert = AlertDialog.Builder(this)
        val editTextNewPassword = EditText(this)

        alert.setTitle("Reset password")
        alert.setMessage("Enter email address")
        alert.setView(editTextNewPassword)

        alert.setPositiveButton("Send mail") { _, _ ->
            val newPassword = editTextNewPassword.text.toString().trim()
            auth.sendPasswordResetEmail(newPassword).addOnCompleteListener { reset ->
                if (reset.isSuccessful) {
                    Toast.makeText(this, "Link sent to email", LENGTH_SHORT).show()
                }
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun inputCheck(): Boolean {
        return when {
            inputEmail.text.toString().isEmpty() -> {
                Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
                false
            }
            !validateEmailFormat(inputEmail.text.toString()) -> {
                Toast.makeText(this, "Email format is incorrect", Toast.LENGTH_LONG).show()
                false
            }
            inputPassword.text.toString().isEmpty() -> {
                Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show()
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
}