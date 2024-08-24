package com.example.e_commerce

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.databinding.ActivityChooseProfilePictureBinding
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import java.io.ByteArrayOutputStream

class ChooseProfilePictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseProfilePictureBinding
    private lateinit var profilePic: ImageView
    private lateinit var nextBtn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProfilePictureBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        profilePic = binding.profilePic

        binding.ChooseLaterBtn.setOnClickListener {
            val i = Intent(this, UserActivity::class.java)
            startActivity(i)
        }

        setImageSelector()

        nextBtn = binding.nextBtn
        nextBtn.setOnClickListener {
            uploadProfilePicToStorage()
        }
    }

    private fun setImageSelector() {
        val changeImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    profilePic.setImageURI(imgUri)
                    nextBtn.isEnabled = true
                }
            }
        binding.ChoosePictureBtn.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }


    }

    private fun uploadProfilePicToStorage() {

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
                                startActivity(Intent(this@ChooseProfilePictureActivity, UserActivity::class.java))
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