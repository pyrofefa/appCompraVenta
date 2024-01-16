package com.esteban.appcomprayventa.options_login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
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
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnSignIn.setOnClickListener {
            validateInfo()
        }

        binding.txtSigUp.setOnClickListener {
            startActivity(Intent(this@LoginEmailActivity, RegisterActivity::class.java))
        }
    }

    private var email = ""
    private var password = ""
    private fun validateInfo() {
        email = binding.editTextEmail.text.toString().trim()
        password = binding.editTextPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editTextEmail.error = "Email Invalid"
            binding.editTextEmail.requestFocus()
        }else if(email.isEmpty()){
            binding.editTextEmail.error = "Enter Email"
            binding.editTextEmail.requestFocus()
        }else if(password.isEmpty()){
            binding.editTextPassword.error = "Enter Password"
            binding.editTextPassword.requestFocus()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Login...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this,"Welcome", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to log in ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}