package com.esteban.appcomprayventa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esteban.appcomprayventa.databinding.ActivityMainBinding
import com.esteban.appcomprayventa.fragments.accountFragment
import com.esteban.appcomprayventa.fragments.adsFragment
import com.esteban.appcomprayventa.fragments.chatsFragment
import com.esteban.appcomprayventa.fragments.homeFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkSession()

        viewFragmentHome()

        binding.ButtonNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_home->{
                    viewFragmentHome()
                    true
                }
                R.id.item_chats->{
                    viewFragmentChats()
                    true
                }
                R.id.item_ads->{
                    viewFragmentMyAds()
                    true
                }
                R.id.item_account->{
                    viewFragmentMyAccount()
                    true
                }
                else ->{
                    false
                }
            }
        }
    }
    private fun checkSession(){
        if(firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginOptions::class.java))
            finishAffinity()
        }
    }
    private fun viewFragmentHome(){
        binding.tittleRl.text = "Home"
        val fragment = homeFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentLayout1.id, fragment, "homeFragment")
        fragmentTransition.commit()
    }
    private fun viewFragmentChats(){
        binding.tittleRl.text = "Chats"
        val fragment = chatsFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentLayout1.id, fragment, "chatsFragment")
        fragmentTransition.commit()
    }
    private fun viewFragmentMyAccount(){
        binding.tittleRl.text = "My Account"
        val fragment = accountFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentLayout1.id, fragment, "accountFragment")
        fragmentTransition.commit()
    }
    private fun viewFragmentMyAds(){
        binding.tittleRl.text = "My ADS"
        val fragment = adsFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentLayout1.id, fragment, "adsFragment")
        fragmentTransition.commit()
    }

}