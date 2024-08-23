package com.example.e_commerce.utils

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.e_commerce.models.Product
import com.example.e_commerce.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseUtil {

    val auth: FirebaseAuth = Firebase.auth

    private val database = Firebase.database
    val productsRef = database.getReference("Products")
    val usersRef = database.getReference("Users")
    val ordersRef = database.getReference("Orders")

    var user: User? = null
    var products: List<Product>? = null

    private val storage = Firebase.storage
    private var storageRef = storage.reference
    var productsImageRef = storageRef.child("Products")
    val profilePicturesRef = storageRef.child("ProfilePictures")

    fun isValidPassword(passwordEditText: EditText): Boolean {
        val password = passwordEditText.text.toString().trim()
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$"

        if(password.isEmpty()) {
            passwordEditText.error = "Please fill out this field"
            passwordEditText.requestFocus()
            return false
        }

        if(!password.matches(passwordPattern.toRegex())) {
            passwordEditText.error = "Password must be at least 8 characters long and include a mix of upper and lower case letters and numbers"
            passwordEditText.requestFocus()
            return false
        }

        return true
    }

    fun isEditTextEmpty(edittext: EditText) : Boolean {
        val text = edittext.text.toString().trim()
        if (text.isEmpty()) {
            edittext.error = "Please fill out this field"
            edittext.requestFocus()
            return true
        }
        return false
    }

    fun sendEmailVerification(context: Context) {

        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener() { task ->


                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Verification email sent to ${user.email} ",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun addProductToCart(productUid: String?, context: Context) {

        val cartList = user!!.cart
        Log.e("Cart", cartList.toString())
        productUid?.let {
            if(cartList.contains(it)) {
                Toast.makeText(context,
                    "Product already in cart.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cartList.add(it)
                Log.e("Cart", cartList.toString())
                Toast.makeText(context,
                    "Product added to cart.",
                    Toast.LENGTH_SHORT
                ).show()
                usersRef.child(auth.uid!!).child("cart").setValue(cartList)
            }
        }
    }

    fun removeFromCart(productUid: String?, context: Context) {
        val cartList = user!!.cart
        Log.e("Cart", cartList.toString())
        productUid?.let {

            if(cartList.remove(it)) {
                Toast.makeText(context,
                    "Product removed from cart.",
                    Toast.LENGTH_SHORT
                ).show()
                usersRef.child(auth.uid!!).child("cart").setValue(cartList)
            } else {
                Log.e("Cart", cartList.toString())
                Toast.makeText(context,
                    "Product is not in cart.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}