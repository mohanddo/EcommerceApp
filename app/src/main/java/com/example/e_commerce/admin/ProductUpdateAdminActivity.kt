package com.example.e_commerce.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityProductUpdateAdminBinding
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.Util
import com.example.e_commerce.utils.Util.setSpinner
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProductUpdateAdminActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityProductUpdateAdminBinding
    private lateinit var name : EditText
    private lateinit var price : EditText
    private lateinit var description : EditText
    private lateinit var productImage: ImageView
    private lateinit var uid: String
    private lateinit var productName: String
    private lateinit var productPrice: String
    private lateinit var productDescription: String
    private lateinit var productType: String
    private lateinit var newProductType: String
    private lateinit var progressBar: ProgressBar
    private lateinit var updateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductUpdateAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        productName = intent.getStringExtra("name")!!
        productPrice = intent.getStringExtra("price")!!
        productDescription = intent.getStringExtra("description")!!
        productType = intent.getStringExtra("type")!!
        uid = intent.getStringExtra("uid")!!

        name = binding.productName
        price = binding.productPrice
        description = binding.productDescription
        progressBar = binding.progressBar

        setProductFields()

        productImage = binding.productImage
        setImageSelector(productImage)

        val backBtn = binding.backButton
        backBtn.setOnClickListener {
            finish()
        }

        updateBtn = binding.UpdateProduct
        updateBtn.setOnClickListener {
            if(!FirebaseUtil.isEditTextEmpty(name) && !FirebaseUtil.isEditTextEmpty(price) && !FirebaseUtil.isEditTextEmpty(description)) {

                updateFieldsInFirebase()

            }
        }


        val deleteBtn = binding.DeleteBtn
        deleteBtn.setOnClickListener {
            Util.showAlertDialog(this,
                "Delete Item", "Are you sure you want to delete this item.") {
                FirebaseUtil.productsRef.child(uid).removeValue()
                FirebaseUtil.productsImageRef.child(uid).delete()
                finish()
            }
        }

        val spinner = binding.productType
        spinner.onItemSelectedListener = this
        setSpinner(spinner, this)
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == productType) {
                spinner.setSelection(i)
                break
            }
        }
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        newProductType = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }



    private fun setProductFields() {
        name.setText(productName)
        price.setText(productPrice)
        description.setText(productDescription)
        Picasso.get().load(intent.getStringExtra("imageLink")).into(binding.productImage)
    }

    private fun setImageSelector(image: ImageView) {
        val cardView = binding.cardView

        val changeImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    image.setImageURI(imgUri)
                }
            }

        cardView.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }

    private fun updateImageInFirebaseStorage() {
        productImage.isDrawingCacheEnabled = true
        productImage.buildDrawingCache()
        val bitmap = (productImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val productImageRef = FirebaseUtil.productsImageRef.child(uid)
        val uploadTask = productImageRef.putBytes(data)
        uploadTask.addOnFailureListener {

            progressBar.visibility = View.GONE
            updateBtn.visibility = View.VISIBLE

        }.addOnSuccessListener { _ ->
            val i = Intent(this, ProductsAdminActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun updateFieldsInFirebase() {
        val updatedFields = mutableMapOf<String, Any>()
        if (name.text.toString() != productName) {
            updatedFields["name"] = name.text.toString()
        }

        if (price.text.toString() != productPrice) {
            updatedFields["price"] = price.text.toString().toDouble()
        }

        if (description.text.toString() != productDescription) {
            updatedFields["description"] = description.text.toString()
        }

        if (newProductType != productType) {
            updatedFields["type"] = newProductType
        }

        progressBar.visibility = View.VISIBLE
        updateBtn.visibility = View.GONE

        FirebaseUtil.productsRef.child(uid).updateChildren(updatedFields).addOnSuccessListener {
                updateImageInFirebaseStorage()
        }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                updateBtn.visibility = View.VISIBLE
        }


    }
}