package com.example.e_commerce

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.admin.ProductsAdminActivity
import com.example.e_commerce.models.Product
import com.example.e_commerce.models.User
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.productsRef
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    private var isUserFetched = false
    private var areProductsFetched = false
    private var timerComplete = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentUser = Firebase.auth.currentUser
        val splashScreenDuration = 2000L

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (currentUser == null) {
                navigateToWelcome()
            }

            if (isUserFetched && areProductsFetched) {
                navigateToHome()
            } else {
                timerComplete = true
            }
        }, splashScreenDuration)

        if(currentUser != null) {
            fetchUserData()
            fetchProducts()
        }
    }



    private fun fetchUserData() {
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(User::class.java)?.let {
                        FirebaseUtil.user = it
                        isUserFetched = true
                        if (timerComplete && areProductsFetched) {
                            navigateToHome()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
                }
            }

            usersRef.child(FirebaseUtil.auth.uid!!).addListenerForSingleValueEvent(userListener)
    }

    private fun fetchProducts() {
        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val products = dataSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
                FirebaseUtil.products = products
                areProductsFetched = true
                if (timerComplete && isUserFetched) {
                    navigateToHome()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadProducts:onCancelled", databaseError.toException())
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this@SplashScreen, UserActivity::class.java)
        startActivity(intent)
        Log.e("Home", "Going to home")
        finish()
    }

    private fun navigateToWelcome() {
        val intent = Intent(this@SplashScreen, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}