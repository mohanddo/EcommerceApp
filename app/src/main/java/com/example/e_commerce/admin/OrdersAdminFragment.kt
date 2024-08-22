package com.example.e_commerce.admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.adapters.CartAdapter
import com.example.e_commerce.adapters.OrderAdapter
import com.example.e_commerce.adapters.OrderAdminAdapter
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.databinding.FragmentOrdersAdminBinding
import com.example.e_commerce.databinding.FragmentOrdersBinding
import com.example.e_commerce.models.Order
import com.example.e_commerce.models.Product
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.ordersRef
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class OrdersAdminFragment : Fragment(R.layout.fragment_orders_admin) {

    private var _binding: FragmentOrdersAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var orders: List<Order>
    private lateinit var orderAdminAdapter: OrderAdminAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrdersAdminBinding.bind(view)

        val chipGroup = binding.chipGroup
        chipGroup.setOnCheckedChangeListener { group, checkedId ->

            val chip: Chip? = group.findViewById(checkedId)
            chip?.let {
                val selectedText = it.text.toString().lowercase()

                orderAdminAdapter.updateOrders(filterContent(orders, selectedText))

            }
        }


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

                orders = dataSnapshot.children.mapNotNull { it.getValue(Order::class.java) }

                if (orders.isEmpty()) {
                    binding.noOrdersText.visibility = View.VISIBLE
                }
                orderAdminAdapter = OrderAdminAdapter(requireContext(), orders)
                recyclerView.adapter = orderAdminAdapter
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

    private fun filterContent(
        ordersList: List<Order>,
        selectedState: String
    ): List<Order> {

        if (selectedState == "all") {
            return ordersList
        }

        val filteredList = mutableListOf<Order>()

        ordersList.forEach {
            if (it.state == selectedState) {
                filteredList.add(it)
            }
        }

        return filteredList.toList()
    }
}