package com.example.e_commerce.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityAddProductBinding
import com.example.e_commerce.models.Product
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.Util
import java.io.ByteArrayOutputStream


class AddProductActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var productImage: ImageView
    private lateinit var productType: String
    private lateinit var addProduct: Button
    private lateinit var productImageLink: String
    private lateinit var progressBar: ProgressBar
    private var isImageSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = binding.progressBar

        val spinner = binding.productType
        spinner.onItemSelectedListener = this
        Util.setSpinner(spinner, this)

        productImage = binding.productImage
        setImageSelector()

        val backButton = binding.backButton
        backButton.setOnClickListener {
            finish()
        }

        addProduct = binding.addProduct
        addProduct.setOnClickListener {
            val name = binding.productName
            val price = binding.productPrice
            val description = binding.productDescription
            if(!isImageSelected) {
                Toast.makeText(
                    this,
                    "Please select an image",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if(!FirebaseUtil.isEditTextEmpty(name) && !FirebaseUtil.isEditTextEmpty(price) && !FirebaseUtil.isEditTextEmpty(description)) {
                addProduct(name.text.toString(),
                    price.text.toString().toDouble(),
                    description.text.toString(),
                    productType)
            }
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            productType = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun addProduct(name: String, price: Double, description: String, type: String) {
        progressBar.visibility = View.VISIBLE
        addProduct.isEnabled = false
        val productRef = FirebaseUtil.productsRef.push()
        val uniqueID = productRef.key
        uploadImageToStorage(uniqueID!!, name, price, description, type)

    }

    private fun uploadImageToStorage(uniqueID: String, name: String, price: Double, description: String, type: String) {

        productImage.isDrawingCacheEnabled = true
        productImage.buildDrawingCache()
        val bitmap = (productImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val productImageRef = FirebaseUtil.productsImageRef.child(uniqueID)
        val uploadTask = productImageRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            Toast.makeText(
                baseContext,
                "Image upload failed: ${exception.message.toString()}",
                Toast.LENGTH_LONG
            ).show()
            Log.e("Imgage upload failed", exception.message.toString())
            progressBar.visibility = View.GONE
            addProduct.isEnabled = true
        }.addOnSuccessListener { taskSnapshot ->

            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                productImageLink = uri.toString()
                setProductInDatabase(uniqueID, name, price, description, type, productImageLink)
            }?.addOnFailureListener { exception ->

                Toast.makeText(
                    baseContext,
                    "Failed to get download URL: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ImageUpload", "Failed to get download URL", exception)
                progressBar.visibility = View.GONE
                addProduct.isEnabled = true
            }
        }
    }

    private fun setProductInDatabase(uniqueID: String, name: String, price: Double, description: String, type: String, imageUrl: String) {
        val product = Product(uniqueID, name, price, description, type, imageUrl)
        val productRef = FirebaseUtil.productsRef.child(uniqueID)
        productRef.setValue(product).addOnSuccessListener {

            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(
                baseContext,
                "Failed to add product: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
            Log.e("AddProduct", "Failed to add product", exception)
            progressBar.visibility = View.GONE
            addProduct.isEnabled = true
        }
    }

    private fun setImageSelector() {
        val cardView = binding.cardView

        val changeImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    productImage.setImageURI(imgUri)
                    isImageSelected = true
                }
            }

        cardView.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }
}
