package com.example.e_commerce.user


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.adapters.ProductAdapter
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.user

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Set the username text
        val nameString = getString(R.string.hello_user, user?.name)
        binding.username.setText(nameString)

        // Initialize the RecyclerView and set up the GridLayoutManager with 2 columns
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Set up the adapter
        val productAdapter = ProductAdapter(requireContext(), products!!)
        recyclerView.adapter = productAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
