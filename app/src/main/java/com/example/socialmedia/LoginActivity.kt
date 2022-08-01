package com.example.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
        if (auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
        loginBtn.setOnClickListener {
            val email=etEmailId.text.toString()
            val password=etpasswordId.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Email and Password Empty Not Allow",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
                .addOnFailureListener{
                    Toast.makeText(this,"Email and Password Are Not Match",Toast.LENGTH_SHORT).show()
                }
        }
    }
}