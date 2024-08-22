package com.example.e_commerce.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.admin.ProductUpdateAdminActivity
import com.example.e_commerce.models.Product
import com.example.e_commerce.user.ProductDetailsActivity
import com.example.e_commerce.utils.FirebaseUtil.addProductToCart
import com.squareup.picasso.Picasso

class ProductAdapter(private val context: Context, private var dataSet: List<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val productContainer: CardView = view.findViewById(R.id.productContainer)
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val addToCartBtn: ImageButton = view.findViewById(R.id.addToCartBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_row_item, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val priceString = context.getString(R.string.product_price, dataSet[position].price)
        holder.productPrice.text = priceString
        holder.productName.text = dataSet[position].name
        Picasso.get().load(dataSet[position].imageLink).into(holder.productImage)
        holder.addToCartBtn.setOnClickListener {
            addProductToCart(dataSet[position].uid, context)
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

//    fun updateProducts(newProducts: MutableList<Product>) {
//        dataSet = newProducts
//        notifyDataSetChanged()
//    }
//
}