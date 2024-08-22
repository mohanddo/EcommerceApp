package com.example.e_commerce.user

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityProductDetailsBinding
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.squareup.picasso.Picasso

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        productImage = binding.productImage
        productName = binding.productName
        productPrice = binding.productPrice
        productDescription = binding.productDescription

        setUpProductInformation()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addToCartBtn.setOnClickListener {
            FirebaseUtil.addProductToCart(intent.getStringExtra("uid"), this)
        }

        binding.removeFromCartBtn.setOnClickListener {
            FirebaseUtil.removeFromCart(intent.getStringExtra("uid") , this)
        }

    }

    private fun setUpProductInformation() {
        productName.text = intent.getStringExtra("name")
        val priceString = this.getString(R.string.product_price, intent.getStringExtra("price")!!.toDouble())
        productPrice.text = priceString
        productDescription.text = intent.getStringExtra("description")
        Picasso.get().load(intent.getStringExtra("imageLink")).into(productImage)
    }
}