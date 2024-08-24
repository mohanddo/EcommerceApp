package com.example.e_commerce.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.adapters.ProductAdminAdapter
import com.example.e_commerce.databinding.FragmentHomeAdminBinding
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.models.Product
import com.example.e_commerce.utils.FirebaseUtil
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class HomeAdminFragment : Fragment(R.layout.fragment_home_admin) {

    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsList: MutableList<Product>
    private lateinit var productAdminAdapter: ProductAdminAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeAdminBinding.bind(view)

        val chipGroup = binding.chipGroup
        chipGroup.setOnCheckedChangeListener { group, checkedId ->

            val chip: Chip? = group.findViewById(checkedId)
            chip?.let {
                val selectedText = it.text.toString()

                productAdminAdapter.updateProducts(filterContent(productsList, selectedText))
            }
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        val productsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.noProductsText.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                productsList = mutableListOf()

                for (productSnapshot in dataSnapshot.children) {

                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        Log.e("Product", product.toString())
                        productsList.add(product)
                    }
                }

                if (productsList.isEmpty()) {
                    binding.noProductsText.visibility = View.VISIBLE
                }

                productAdminAdapter = ProductAdminAdapter(requireContext(), productsList)
                recyclerView.adapter = productAdminAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.e("Error", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseUtil.productsRef.addValueEventListener(productsListener)

        val addProductBtn = binding.addProduct
        addProductBtn.setOnClickListener {
            val i = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(i)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun filterContent(
        productList: MutableList<Product>,
        selectedType: String
    ): MutableList<Product> {

        if (selectedType == "All") {
            return productList
        }

        val filteredList = mutableListOf<Product>()

        productList.forEach {
            if (it.type == selectedType) {
                filteredList.add(it)
            }
        }

        return filteredList
    }
}
