package com.esteban.appcomprayventa

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import com.esteban.appcomprayventa.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait a second")
        progressDialog.setCanceledOnTouchOutside(false)

        loadInfo()

        binding.floatingButtonChangeImg.setOnClickListener {
            selectImageFrom()
        }
    }

    private fun loadInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val names = "${snapshot.child("name").value}"
                    val image = "${snapshot.child("urlImageProfile").value}"
                    val birthdate = "${snapshot.child("birthdate").value}"
                    val phone = "${snapshot.child("phone").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"


                    /* Set de information */
                    binding.editTextNames.setText(names)
                    binding.editTextBirthday.setText(birthdate)
                    binding.editTextPhone.setText(phone)


                    try {
                        Glide.with(applicationContext)
                            .load(image)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgProfile)
                    }catch (e: Exception){
                        Toast.makeText(this@EditProfileActivity,"${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    try {
                        val code = phone.replace("+","").toInt()
                        binding.codeSelect.setCountryForPhoneCode(code)
                    }catch (e: Exception) {
                        Toast.makeText(this@EditProfileActivity, "${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun selectImageFrom(){
        val popupMenu = PopupMenu(this, binding.floatingButtonChangeImg)
        popupMenu.menu.add(Menu.NONE,1,1,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val itemId = item.itemId
            if(itemId == 1){

            }else if(itemId == 2){

            }

            return@setOnMenuItemClickListener true

        }
    }
}