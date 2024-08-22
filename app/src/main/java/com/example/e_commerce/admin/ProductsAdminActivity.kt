package com.example.e_commerce.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.adapters.ProductAdminAdapter
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityProductsAdminBinding
import com.example.e_commerce.models.Product
import com.example.e_commerce.user.OrdersFragment
import com.example.e_commerce.user.ProfileFragment
import com.example.e_commerce.utils.FirebaseUtil
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProductsAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductsAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val homeFragment = HomeAdminFragment()
        val ordersFragment = OrdersAdminFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home_admin -> setCurrentFragment(homeFragment)
                R.id.navigation_shipping_admin -> setCurrentFragment(ordersFragment)
                R.id.navigation_person_admin -> setCurrentFragment(profileFragment)
            }
            true
        }
    }


    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentUser, fragment)
            commit()
        }
    }
}
