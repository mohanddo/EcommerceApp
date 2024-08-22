package com.example.e_commerce.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.models.Product
import com.example.e_commerce.user.ProductDetailsActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.Util
import com.squareup.picasso.Picasso

class CartAdapter(private val context: Context, private var dataSet: MutableList<Product>): RecyclerView.Adapter<CartAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val productContainer: CardView = view.findViewById(R.id.productContainer)
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_admin_cart_row_item, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val priceString = context.getString(R.string.product_price, dataSet[position].price)
        holder.productPrice.text = priceString
        holder.productName.text = dataSet[position].name
        Picasso.get().load(dataSet[position].imageLink).into(holder.productImage)
        holder.deleteBtn.setOnClickListener {
            FirebaseUtil.removeFromCart(dataSet[position].uid, context)
        }

        holder.productContainer.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("uid", dataSet[position].uid)
            intent.putExtra("name", dataSet[position].name)
            intent.putExtra("price", dataSet[position].price.toString())
            intent.putExtra("description", dataSet[position].description)
            intent.putExtra("imageLink", dataSet[position].imageLink)
            startActivity(context, intent, null)
        }


    }

    override fun getItemCount() = dataSet.size
}