package com.example.e_commerce.user

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.adapters.CartAdapter
import com.example.e_commerce.adapters.OrderAdapter
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.databinding.FragmentOrdersBinding
import com.example.e_commerce.models.Order
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.ordersRef
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class OrdersFragment : Fragment(R.layout.fragment_orders) {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrdersBinding.bind(view)

        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!isAdded || context == null) {
                    return
                }

                binding.progressBar.visibility = View.GONE

                val allOrders = dataSnapshot.children.mapNotNull { it.getValue(Order::class.java) }
                val userOrders = allOrders.filter { it.uid == auth.uid!! }

                if (userOrders.isEmpty()) {
                    binding.noOrdersText.visibility = View.VISIBLE
                }
                val orderAdapter = OrderAdapter(requireContext(), userOrders)
                recyclerView.adapter = orderAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadCart:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}