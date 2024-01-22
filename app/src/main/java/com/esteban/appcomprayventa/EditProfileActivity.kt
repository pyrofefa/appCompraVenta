package com.esteban.appcomprayventa

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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.esteban.appcomprayventa.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri: Uri? = null
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
            .addValueEventListener(object : ValueEventListener {
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
                    } catch (e: Exception) {
                        Toast.makeText(this@EditProfileActivity, "${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    try {
                        val code = phone.replace("+", "").toInt()
                        binding.codeSelect.setCountryForPhoneCode(code)
                    } catch (e: Exception) {
                        Toast.makeText(this@EditProfileActivity, "${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun loadImageStorage(){
        progressDialog.setMessage("Upload Image")
        progressDialog.show()

        val pathImage = "profileImages/"+ firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(pathImage)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapShot->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImageLoad = uriTask.result.toString()
                if (uriTask.isSuccessful){
                    updateImageBD(urlImageLoad)
                }
            }
            .addOnFailureListener{ error ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImageBD(urlImageLoad: String) {
        progressDialog.setMessage("Update image...")
        progressDialog.show()

        val hasMap : HashMap<String, Any> = HashMap()
        if (imageUri != null) {
            hasMap["urlImageProfile"] = urlImageLoad
        }

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hasMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Profile Image update", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { error ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectImageFrom() {
        val popupMenu = PopupMenu(this, binding.floatingButtonChangeImg)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {
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
            } else if (itemId == 2) {
                //Galeria
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    galleryImage()
                } else {
                    grantStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            return@setOnMenuItemClickListener true
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

    private val grantStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { result ->
            if (result) {
                galleryImage()
            } else {
                Toast.makeText(this, "permission storage denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val resultCameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadImageStorage()
                /*try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgProfile)
                } catch (e: Exception) {

                }*/
            } else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }
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

    private val resultGalleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data?.data
                loadImageStorage()

                /*try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgProfile)
                } catch (e: Exception) {

                }*/

            }else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }
        }
    }