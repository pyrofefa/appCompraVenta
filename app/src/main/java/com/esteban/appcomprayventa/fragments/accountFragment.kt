package com.esteban.appcomprayventa.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.esteban.appcomprayventa.Constants
import com.esteban.appcomprayventa.EditProfileActivity
import com.esteban.appcomprayventa.LoginOptions
import com.esteban.appcomprayventa.R
import com.esteban.appcomprayventa.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class accountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        readInfo()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(mContext, EditProfileActivity::class.java))
        }

        binding.btnSignOff.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext,LoginOptions::class.java))
            activity?.finishAffinity()
        }
    }

    private fun readInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val names = "${snapshot.child("name").value}"
                    val email = "${snapshot.child("email").value}"
                    val image = "${snapshot.child("urlImageProfile").value}"
                    val birthdate = "${snapshot.child("birthdate").value}"
                    var time = "${snapshot.child("time").value}"
                    val phone = "${snapshot.child("phone").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val supplier = "${snapshot.child("supplier").value}"

                    if (time == "null"){
                        time = "0"
                    }

                    val formatTime = Constants.getDate(time.toLong())

                    /* Set de information */
                    binding.textViewEmail.text = email
                    binding.textViewNames.text = names
                    binding.textViewBirthday.text = birthdate
                    binding.textViewPhone.text = phoneCode + phone
                    binding.textViewMemberSince.text = formatTime

                    /* Set de image */
                    try {
                        Glide.with(mContext)
                            .load(image)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.ImageViewProfile)
                    }catch (e:Exception){
                        Toast.makeText(mContext,"${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    if (supplier == "Email"){
                        val isVerified = firebaseAuth.currentUser!!.isEmailVerified
                        if (isVerified){
                            binding.textViewAccountStatus.text = "Verified"
                        }else{
                            binding.textViewAccountStatus.text = "Not Verified"
                        }
                    }else {
                        binding.textViewAccountStatus.text = "Verified"
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}