package com.esteban.appcomprayventa.options_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esteban.appcomprayventa.LoginOptions
import com.esteban.appcomprayventa.MainActivity
import com.esteban.appcomprayventa.R
import com.esteban.appcomprayventa.RegisterActivity
import com.esteban.appcomprayventa.databinding.ActivityLoginEmailBinding
import com.esteban.appcomprayventa.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtSigUp.setOnClickListener {
            startActivity(Intent(this@LoginEmailActivity, RegisterActivity::class.java))
        }
    }
}