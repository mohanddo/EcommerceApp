package com.example.e_commerce

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.admin.ProductsAdminActivity
import com.example.e_commerce.databinding.ActivityEditAccountBinding
import com.example.e_commerce.user.ProfileFragment
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import java.io.ByteArrayOutputStream

class EditAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAccountBinding
    private lateinit var profilePic: ImageView
    private var isProfilePicSelected = false
    private lateinit var profilePicUri: Uri
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = binding.progressBar

        profilePic = binding.profilePic
        setProfilePicSelector()

        binding.CancelBtn.setOnClickListener {
            finish()
        }

        val fullNameEditText = binding.FullNameEditText
        val originalFullName = fullNameEditText.text
        fullNameEditText.setText(intent.getStringExtra("FullName")!!)

        val saveBtn = binding.SaveBtn
        saveBtn.setOnClickListener {
            if (!FirebaseUtil.isEditTextEmpty(fullNameEditText)) {
                val updatedFields = mutableMapOf<String, Any>()
                val intent = Intent(this, UserActivity::class.java)
                if (originalFullName == fullNameEditText.text) {

                    updatedFields["name"] = fullNameEditText.text.toString()
                    intent.putExtra("NewName", fullNameEditText.text.toString())
                }

                progressBar.visibility = View.VISIBLE
                usersRef.child(auth.uid!!).child("name").updateChildren(updatedFields).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        if(isProfilePicSelected) {
                            uploadProfilePicToStorage(intent)
                        } else {
                            startActivity(intent)
                            finish()
                        }
                    } else {

                    }
                }
            }
        }

    }

    private fun setProfilePicSelector() {
        val changeImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    profilePicUri = data?.data!!
                    profilePic.setImageURI(profilePicUri)
                    isProfilePicSelected = true
                }
            }
        binding.EditBtn.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }

    private fun uploadProfilePicToStorage(intent: Intent) {

        profilePic.isDrawingCacheEnabled = true
        profilePic.buildDrawingCache()
        val bitmap = (profilePic.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = FirebaseUtil.profilePicturesRef.child(auth.uid!!).putBytes(data)
        uploadTask.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                task.result?.storage?.downloadUrl?.addOnCompleteListener { uriTask ->
                    if (uriTask.isSuccessful) {
                        val downloadUri = uriTask.result.toString()
                        usersRef.child(auth.uid!!).child("profilePic").setValue(downloadUri).addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                progressBar.visibility = View.GONE
                                intent.putExtra("NewProfilePicUri", profilePicUri)
                                startActivity(intent)
                                finish()
                            } else {

                            }
                        }
                    } else {
                        Log.e("Upload", "Failed to get download URL", uriTask.exception)
                    }
                }
            } else {

            }
        }
    }
}