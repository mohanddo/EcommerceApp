package com.example.e_commerce.user

import GridSpacingItemDecoration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.adapters.ProductAdapter
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.models.Product
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.productsRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.e_commerce.utils.FirebaseUtil.user

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val nameString = getString(R.string.hello_user, user?.name)

        user?.profilePic?.let {
            Picasso.get().load(it).into(binding.profilePic)
        }
        binding.username.setText(nameString)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val productAdapter = ProductAdapter(requireContext(), products!!)
        recyclerView.adapter = productAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}