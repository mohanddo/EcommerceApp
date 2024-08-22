package com.example.e_commerce.user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val homeFragment = HomeFragment()
        val cartFragment = CartFragment()
        val ordersFragment = OrdersFragment()
        val profileFragment = ProfileFragment()

        val bottomNavigationView = binding.bottomNavigationView

        if (intent.getStringExtra("NewName") == null) {
            setCurrentFragment(homeFragment)
        } else {
            val bundle = Bundle()
            bundle.putString("FullName", intent.getStringExtra("NewName"))
            profileFragment.arguments = bundle
            bottomNavigationView.selectedItemId = R.id.navigation_person
            setCurrentFragment(profileFragment)
        }


        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> setCurrentFragment(homeFragment)
                R.id.navigation_cart -> setCurrentFragment(cartFragment)
                R.id.navigation_shipping -> setCurrentFragment(ordersFragment)
                R.id.navigation_person -> setCurrentFragment(profileFragment)
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