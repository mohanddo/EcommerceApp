package com.example.e_commerce.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.models.Order
import com.example.e_commerce.models.Product
import com.example.e_commerce.user.ProductDetailsActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.ordersRef
import com.example.e_commerce.utils.Util
import com.squareup.picasso.Picasso

class OrderAdminAdapter(private val context: Context, private var dataSet: List<Order>): RecyclerView.Adapter<OrderAdminAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val fullName: TextView = view.findViewById(R.id.fullName)
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val wilayaAndRegion: TextView = view.findViewById(R.id.wilayaAndRegion)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        val productNames: TextView = view.findViewById(R.id.productNames)
        val orderState: TextView = view.findViewById(R.id.OrderState)
        val shipmentType: TextView = view.findViewById(R.id.shipmentType)
        val cancelOrder: AppCompatButton = view.findViewById(R.id.cancelOrder)
        val confirmShipment: AppCompatButton = view.findViewById(R.id.confirmShipment)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_admin_row_item, parent, false)

        return OrderViewHolder(view)
    }

    private val green = ContextCompat.getColor(context, R.color.green)
    private val red = ContextCompat.getColor(context, R.color.red)
    private val orange = ContextCompat.getColor(context, R.color.orange)
    private val lightBlue = ContextCompat.getColor(context, R.color.lightBlue)

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = dataSet[position]
        holder.fullName.text = order.name
        holder.phoneNumber.text = order.phoneNumber
        val string = context.getString(R.string.wilaya_region, order.wilaya, order.commun)
        holder.wilayaAndRegion.text = string
        val totalPrice = context.getString(R.string.product_price, order.totalPrice.toDouble())
        holder.totalPrice.text = totalPrice
        holder.productNames.text = order.productsNames[0]
        holder.orderState.text = order.state
        reactToOrderState(holder, order.state)
        holder.shipmentType.text = order.typeShippment
        holder.cancelOrder.setOnClickListener {
            Util.showAlertDialog(context, "Cancelling Order","Do you really want to cancel this order.", "No") {
                cancelOrder(holder, order.orderId)
            }
        }
        holder.confirmShipment.setOnClickListener {
            Util.showAlertDialog(context, "Confirm Shipment","Do you really want to set this product for shipment.", "No") {
                confirmShipment(holder, order.orderId)
            }
        }
    }

    override fun getItemCount() = dataSet.size

    private fun cancelOrder(holder: OrderViewHolder, orderId: String) {
        val updates = mapOf<String, Any>(
            "state" to "cancelled"
        )
        holder.cancelOrder.visibility = View.GONE
        holder.progressBar.visibility = View.VISIBLE
        ordersRef.child(orderId).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                holder.orderState.text = context.getString(R.string.cancelled)
                holder.orderState.backgroundTintList = ColorStateList.valueOf(red)
                holder.confirmShipment.visibility = View.GONE
            } else {
                val exception = task.exception
                Toast.makeText(context, "Failed to cancel order: ${exception?.message}", Toast.LENGTH_LONG).show()
                holder.cancelOrder.visibility = View.VISIBLE
            }
            holder.progressBar.visibility = View.GONE
        }
    }

    private fun confirmShipment(holder: OrderViewHolder, orderId: String) {
        val updates = mapOf<String, Any>(
            "state" to "shipped"
        )
        holder.confirmShipment.visibility = View.GONE
        holder.progressBar.visibility = View.VISIBLE
        ordersRef.child(orderId).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                holder.orderState.text = context.getString(R.string.shipped)
                holder.orderState.backgroundTintList = ColorStateList.valueOf(orange)
            } else {
                val exception = task.exception
                Toast.makeText(context, "Failed to confirm shipment: ${exception?.message}", Toast.LENGTH_LONG).show()
                holder.confirmShipment.visibility = View.VISIBLE
            }
            holder.progressBar.visibility = View.GONE
        }
    }

    private fun reactToOrderState(holder: OrderViewHolder, orderState: String) {

        when (orderState) {
            "waiting" -> {
                holder.cancelOrder.visibility = View.VISIBLE
                holder.confirmShipment.visibility = View.VISIBLE
                holder.orderState.backgroundTintList = ColorStateList.valueOf(lightBlue)
            }
            "shipped" -> {
                holder.cancelOrder.visibility = View.VISIBLE
                holder.orderState.backgroundTintList = ColorStateList.valueOf(orange)
            }
            "delivered" -> {
                holder.orderState.backgroundTintList = ColorStateList.valueOf(green)
            }
            "cancelled" -> {
                holder.orderState.backgroundTintList = ColorStateList.valueOf(red)
            }
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        dataSet = newOrders
        notifyDataSetChanged()
    }

}