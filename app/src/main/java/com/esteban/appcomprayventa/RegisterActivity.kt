package com.esteban.appcomprayventa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.esteban.appcomprayventa.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait a moment")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegister.setOnClickListener {
            validateInfo()
        }
    }

    private var email = ""
    private var password = ""
    private var repeatPassword = ""

    private fun validateInfo() {
        email = binding.editTextEmail.text.toString().trim()
        password = binding.editTextPassword.text.toString().trim()
        repeatPassword = binding.editTextRepeatPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editTextEmail.error = "Email Invalid"
            binding.editTextEmail.requestFocus()
        }else if(email.isEmpty()){
            binding.editTextEmail.error = "Enter Email"
            binding.editTextEmail.requestFocus()
        }else if(password.isEmpty()){
            binding.editTextPassword.error = "Enter Password"
            binding.editTextPassword.requestFocus()
        }else if(repeatPassword.isEmpty()){
            binding.editTextRepeatPassword.error = "Repeat Password"
            binding.editTextRepeatPassword.requestFocus()
        }else if(password != repeatPassword){
            binding.editTextPassword.error = "Do Not Match"
            binding.editTextPassword.requestFocus()
        }
        else{
            registerUser()
        }
    }

    private fun registerUser() {
        progressDialog.setMessage("Create Account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                addInfo()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"User not register ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addInfo() {
        progressDialog.setMessage("Save data")
        val time = Constants.timeToDevice()
        val emailUser = firebaseAuth.currentUser?.email
        val uidUser = firebaseAuth.uid

        val hasMap = HashMap<String, Any>()
        hasMap["name"] = ""
        hasMap["phoneCode"] = ""
        hasMap["phone"] = ""
        hasMap["urlImageProfile"] = ""
        hasMap["supplier"] = "Email"
        hasMap["writing"] = ""
        hasMap["time"] = time
        hasMap["online"] = true
        hasMap["email"] = "${emailUser}"
        hasMap["uid"] = "${uidUser}"
        hasMap["birthdate"] = ""

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(uidUser!!)
            .setValue(hasMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"User not register ${e.message}", Toast.LENGTH_SHORT)
            }
    }
}