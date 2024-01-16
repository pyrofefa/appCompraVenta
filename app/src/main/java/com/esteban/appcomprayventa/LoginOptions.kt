package com.esteban.appcomprayventa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esteban.appcomprayventa.databinding.ActivityLoginOptionsBinding
import com.esteban.appcomprayventa.options_login.LoginEmailActivity
import com.google.firebase.auth.FirebaseAuth

class LoginOptions : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkSession()

        binding.loginWithEmail.setOnClickListener {
            startActivity(Intent(this@LoginOptions,LoginEmailActivity::class.java))
        }
    }
    private fun checkSession(){
        if(firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}