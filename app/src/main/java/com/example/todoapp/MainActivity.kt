package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
            logInUser()
        }

        buttonRegisterNewUser.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun logInUser() {
        auth.signInWithEmailAndPassword(inputEmail.text.toString(), inputPassword.text.toString()).addOnCompleteListener(this) { login ->

            //TODO add fail checks here
            if (login.isSuccessful) {
                startActivity(Intent(this, TaskListActivity::class.java))
            } else {
                Toast.makeText(this, "Log in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}