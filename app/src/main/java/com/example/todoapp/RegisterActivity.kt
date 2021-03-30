package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(registerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        reference = database

        buttonRegister.setOnClickListener {
            registerNewUser()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun registerNewUser() {
        auth.createUserWithEmailAndPassword(inputNewEmail.text.toString(), inputNewPassword.text.toString())
            .addOnCompleteListener(this) { register ->

                //TODO add fail checks
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
}