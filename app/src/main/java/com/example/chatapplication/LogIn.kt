package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {
    private lateinit  var edtEmail :EditText
    private lateinit  var edtPassword :EditText
    private lateinit  var btnLogin :Button
    private lateinit  var btnSignUp:Button
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        edtEmail=findViewById(R.id.edit_email)
        edtPassword=findViewById(R.id.edit_password)
        btnLogin=findViewById(R.id.button_login)
        btnSignUp=findViewById(R.id.button_signup)
        mAuth= FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener()
        {
            val intent= Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener()
        {
            val email=edtEmail.text.toString()
            val password=edtPassword.text.toString()
            login(email,password);
        }

    }

    private fun login(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // code for jumping to home
                    val intent= Intent(this@LogIn,MainActivity ::class.java)
                    finish()
                    startActivity(intent)


                } else {
                    // If sign in fails, display a message to the user.
                   Toast.makeText(this@LogIn,"user does not exist",Toast.LENGTH_SHORT).show()
                }
            }
    }
}