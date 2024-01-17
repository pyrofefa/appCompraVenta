package com.esteban.appcomprayventa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.esteban.appcomprayventa.databinding.ActivityLoginOptionsBinding
import com.esteban.appcomprayventa.options_login.LoginEmailActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginOptions : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait a moment")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        checkSession()

        val googleSingInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSingInOptions)

        binding.loginWithEmail.setOnClickListener {
            startActivity(Intent(this@LoginOptions,LoginEmailActivity::class.java))
        }

        binding.loginWithGoogle.setOnClickListener {
            googleLogin()
        }
    }

    private fun googleLogin() {
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }
    private val googleSignInARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK){
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                loginWithGoogle(account.idToken)
            }catch (e:Exception){
                Toast.makeText(this,"Welcome", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithGoogle(idToken: String?) {
    val credential = GoogleAuthProvider.getCredential(idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {resultsAuth ->
                if(resultsAuth.additionalUserInfo!!.isNewUser){
                    addInfo()
                }else{
                    startActivity(Intent(this,MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to log in ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkSession(){
        if(firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    private fun addInfo() {
        progressDialog.setMessage("Save data")
        val time = Constants.timeToDevice()
        val emailUser = firebaseAuth.currentUser?.email
        val uidUser = firebaseAuth.uid
        val nameUser = firebaseAuth.currentUser?.displayName

        val hasMap = HashMap<String, Any>()
        hasMap["name"] = "${nameUser}"
        hasMap["phoneCode"] = ""
        hasMap["phone"] = ""
        hasMap["urlImageProfile"] = ""
        hasMap["supplier"] = "Google"
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