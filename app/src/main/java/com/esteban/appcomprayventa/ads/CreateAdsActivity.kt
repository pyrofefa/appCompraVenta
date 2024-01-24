package com.esteban.appcomprayventa.ads

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.esteban.appcomprayventa.Adapters.selectImageAdapter
import com.esteban.appcomprayventa.Constants
import com.esteban.appcomprayventa.Models.SelectImageModel
import com.esteban.appcomprayventa.R
import com.esteban.appcomprayventa.databinding.ActivityCreateAdsBinding
import com.google.firebase.auth.FirebaseAuth
import java.net.URI

class CreateAdsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAdsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri : URI?=null

    private lateinit var imageSelectedArrayList : ArrayList<SelectImageModel>
    private lateinit var adapterImageSelected : selectImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait a moment")
        progressDialog.setCanceledOnTouchOutside(false)

        val adapterCategory = ArrayAdapter(this, R.layout.item_category, Constants.categories)
        binding.autoCompleCategory.setAdapter(adapterCategory)

        val adapterCondition = ArrayAdapter(this, R.layout.item_condition, Constants.conditios)
        binding.autocompleteCondition.setAdapter(adapterCondition)

    }
}