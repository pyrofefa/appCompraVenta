package com.esteban.appcomprayventa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.esteban.appcomprayventa.databinding.ActivityLoginOptionsBinding
import com.esteban.appcomprayventa.options_login.LoginEmailActivity

class LoginOptions : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginWithEmail.setOnClickListener {
            startActivity(Intent(this@LoginOptions,LoginEmailActivity::class.java))
        }
    }
}