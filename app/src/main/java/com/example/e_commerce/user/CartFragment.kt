package com.example.e_commerce.user


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.adapters.CartAdapter
import com.example.e_commerce.adapters.ProductAdapter
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.models.Product
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.productsRef
import com.example.e_commerce.utils.FirebaseUtil.user
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private var price = 0.0
    private lateinit var productNames: MutableList<String>
    private lateinit var cartList: MutableList<Product>
    private lateinit var cart: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)


        binding.price.text = getString(R.string.price, price)

        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager


        usersRef.child(auth.uid!!).child("cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!isAdded || context == null) {
                    return
                }
                price = 0.0
                productNames = mutableListOf()
                binding.CartIsEmptyText.visibility = View.GONE
                binding.progressBar.visibility = View.GONE

                cart = dataSnapshot.children.mapNotNull { it.getValue(String::class.java) }.toTypedArray()
                cartList = products!!.filter { cart.contains(it.uid) }.toMutableList()
                cartList.forEach {
                    price += it.price
                    productNames.add(it.name)
                }
                binding.price.text = getString(R.string.price, price)

                if (cartList.isEmpty()) {
                    binding.CartIsEmptyText.visibility = View.VISIBLE
                }
                val productAdapter = CartAdapter(requireContext(), cartList)
                recyclerView.adapter = productAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadCart:onCancelled", databaseError.toException())
            }
        })

        binding.purchaseBtn.setOnClickListener {
            if(cartList.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Cart is empty",
                    Toast.LENGTH_SHORT
                    ).show()
            } else {
                val intent = Intent(requireContext(), ConfirmOrderActivity::class.java)
                intent.putExtra("TotalPrice", price.toString())
                intent.putExtra("ProductNames", productNames.toTypedArray())
                startActivity(intent)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}