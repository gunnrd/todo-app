package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        auth = FirebaseAuth.getInstance()
        reference = database

        buttonRegisterAndLogin.setOnClickListener {
            registerNewUserAndLogin()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun registerNewUserAndLogin() {
        auth.createUserWithEmailAndPassword(inputNewEmail.text.toString(), inputNewPassword.text.toString())
            .addOnCompleteListener(this) { register ->

                //TODO add fail checks

                if (register.isSuccessful) {
                    startActivity(Intent(this,TaskListActivity::class.java))
                } else {
                    Toast.makeText(this, "Registering failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}