package com.esteban.appcomprayventa.ads

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.esteban.appcomprayventa.Adapters.selectImageAdapter
import com.esteban.appcomprayventa.Constants
import com.esteban.appcomprayventa.Models.SelectImageModel
import com.esteban.appcomprayventa.R
import com.esteban.appcomprayventa.databinding.ActivityCreateAdsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class CreateAdsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAdsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri : Uri?=null

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

        imageSelectedArrayList = ArrayList()
        loadImages()

        binding.addImage.setOnClickListener {
            showOptions()
        }

        binding.btnCreateAds.setOnClickListener {
            validateInfo()
        }

    }

    private fun showOptions() {
        val popupMenu = PopupMenu(this, binding.addImage)
        popupMenu.menu.add(Menu.NONE,1,1, "Camera")
        popupMenu.menu.add(Menu.NONE,2,2, "Gallery")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {item ->
            val itemId = item.itemId
            if (itemId == 1){
                //Camara
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    allowPermissionsCamera.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    allowPermissionsCamera.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            }
            else if(itemId == 2){
                //Galeria
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    galleryImage()
                } else {
                    grantStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            true
        }

    }

    private val grantStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { result ->
            if (result) {
                galleryImage()
            } else {
                Toast.makeText(this, "permission storage denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val resultGalleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data?.data
                val time = "${Constants.timeToDevice()}"
                val modelImageSelected = SelectImageModel(
                    time, imageUri, null, false)
                imageSelectedArrayList.add(modelImageSelected)
                loadImages()
            }else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }
        }

    private val resultCameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val time = "${Constants.timeToDevice()}"
                val modelImageSelected = SelectImageModel(
                    time, imageUri, null, false)
                imageSelectedArrayList.add(modelImageSelected)
                loadImages()
            } else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }
        }

    private val allowPermissionsCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { result ->
            var grantAll = true
            for (isItGranted in result.values) {
                grantAll = grantAll && isItGranted
            }
            if (grantAll) {
                imageCamera()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun loadImages() {
        adapterImageSelected = selectImageAdapter(this,imageSelectedArrayList)
        binding.relativeLayoutImages.adapter = adapterImageSelected
    }
    private fun galleryImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultGalleryActivityResultLauncher.launch(intent)
    }

    private fun imageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Image-tittle")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image-description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultCameraActivityResultLauncher.launch(intent)
    }

    private var brand = "";
    private var category = "";
    private var condition = "";
    private var direction = "";

    private var price = "";
    private var title = "";
    private var description = "";
    private var latitude = 0.0;
    private var longitude = 0.0;

    private fun validateInfo() {
        brand = binding.editTextBrand.text.toString().trim()
        category = binding.autoCompleCategory.text.toString().trim()
        condition = binding.autocompleteCondition.toString().trim()
        direction = binding.autocompleteLocation.text.toString().trim()
        price = binding.editTextPrice.text.toString().trim()
        title = binding.editTextTitle.text.toString().trim()
        description = binding.editTextDescription.text.toString().trim()

       // latitude = binding.editTextPhone.text.toString().trim()
       // longitude = binding.editTextPhone.text.toString().trim()

        if (brand.isEmpty()){
            binding.editTextBrand.error = "Enter a brand"
            binding.editTextBrand.requestFocus()
        }else if(category.isEmpty()){
            binding.autoCompleCategory.error = "Enter a category"
            binding.autoCompleCategory.requestFocus()
        }else if(condition.isEmpty()){
            binding.autocompleteCondition.error = "Enter a condition"
            binding.autocompleteCondition.requestFocus()
        }else if(price.isEmpty()){
            binding.editTextPrice.error = "Enter a price"
            binding.editTextPrice.requestFocus()
        }else if(title.isEmpty()){
            binding.editTextTitle.error = "Enter a Title"
            binding.editTextTitle.requestFocus()
        }else if(description.isEmpty()) {
            binding.editTextDescription.error = "Enter a description"
            binding.editTextDescription.requestFocus()
        }else if(imageUri == null) {
            Toast.makeText(this, "Add at least one image", Toast.LENGTH_SHORT).show()
        }else{
            addADS()
        }
    }

    private fun addADS() {
        progressDialog.setMessage("Add ads")
        progressDialog.show()

        val time = Constants.timeToDevice()
        val ref = FirebaseDatabase.getInstance().getReference("ads")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["brand"] = "${brand}"
        hashMap["category"] = "${category}"
        hashMap["condition"] = "${condition}"
        hashMap["direction"] = "${direction}"
        hashMap["price"] = "${price}"
        hashMap["title"] = "${title}"
        hashMap["description"] = "${description}"
        hashMap["status"] = "${Constants.adsAvailable}"
        hashMap["time"] = "${time}"
        hashMap["latitude"] = "${latitude}"
        hashMap["longitude"] = "${longitude}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                loadImagesStorages(keyId)
            }.addOnFailureListener {error->
                progressDialog.dismiss()
                Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
            }

    }
    private fun loadImagesStorages(keyId: String) {
        for (i in imageSelectedArrayList.indices) {
            val modelImageSelected = imageSelectedArrayList[i]
            val nameImage = modelImageSelected.id
            val pathImage = "ADS/$nameImage"

            val storageReference = FirebaseStorage.getInstance().getReference(pathImage)
            storageReference.putFile(modelImageSelected.imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImgLoaded = uriTask.result

                    if (uriTask.isSuccessful){
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = "${modelImageSelected.imageUri}"
                        hashMap["imageUrl"] = "$urlImgLoaded"

                        val ref = FirebaseDatabase.getInstance().getReference("ads")
                        ref.child(keyId).child("images")
                            .child(nameImage)
                            .updateChildren(hashMap)
                    }
                    progressDialog.dismiss()
                    onBackPressedDispatcher.onBackPressed()
                    Toast.makeText(this, "Add ADS", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener { error ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}