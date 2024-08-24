package com.example.e_commerce.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityConfirmOrderBinding
import com.example.e_commerce.models.Order
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.auth
import com.example.e_commerce.utils.FirebaseUtil.isEditTextEmpty
import com.example.e_commerce.utils.FirebaseUtil.isTextViewEmpty
import com.example.e_commerce.utils.FirebaseUtil.ordersRef
import com.example.e_commerce.utils.FirebaseUtil.user
import com.example.e_commerce.utils.FirebaseUtil.usersRef

class ConfirmOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmOrderBinding
    private lateinit var fullName: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var wilayaTextView: TextView
    private lateinit var typeOfShipmentTextView: TextView
    private lateinit var communeEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var commentEditText: EditText
    private lateinit var confirmBtn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfirmOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener { finish() }

        commentEditText = binding.comment
        communeEditText = binding.commune
        addressEditText = binding.address

        fullName = binding.fullName
        fullName.setText(user!!.name)

        phoneNumber = binding.phoneNumber
        phoneNumber.setText(user!!.phoneNumber)

        val wilayasList = resources.getStringArray(R.array.algeria_wilayas)
        wilayaTextView = binding.wilaya
        binding.wilaya.setOnClickListener {
            alertDialog(wilayasList, "Choose you wilaya", wilayaTextView)
        }

        val typeOfShippmentList = resources.getStringArray(R.array.type_of_shipment_options)
        typeOfShipmentTextView = binding.typeOfShippment
        typeOfShipmentTextView.setOnClickListener {
            alertDialog(typeOfShippmentList, "Select your shipment type", typeOfShipmentTextView)
        }

        confirmBtn = binding.ConfirmBtn
        confirmBtn.setOnClickListener {
            if (areAllFielsNotEmpty()) {
                confirmOrder()
            }
        }
    }

    private fun alertDialog(options: Array<String>, title: String, editText: TextView) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        builder.setSingleChoiceItems(options, -1) { dialogInterface, which ->
            editText.setText(options[which])
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun areAllFielsNotEmpty() : Boolean {
        if (isEditTextEmpty(fullName) || isEditTextEmpty(phoneNumber) || isTextViewEmpty(wilayaTextView)
            || isEditTextEmpty(communeEditText) || isEditTextEmpty(addressEditText) || isTextViewEmpty(typeOfShipmentTextView)
        ) {
            return false
        } else {
            return true
        }
    }

    private fun confirmOrder() {

        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        confirmBtn.visibility = View.GONE

        val orderRef = ordersRef.push()
        val orderId = orderRef.key

        val order = Order(
            auth.uid!!,
            orderId!!,
            fullName.text.toString(),
            phoneNumber.text.toString(),
            wilayaTextView.text.toString(),
            communeEditText.text.toString(),
            addressEditText.text.toString(),
            typeOfShipmentTextView.text.toString(),
            commentEditText.text.toString(),
            intent.getStringExtra("TotalPrice")!!,
            intent.getStringArrayExtra("ProductNames")!!.toList(),
            "waiting"
        )

        orderRef.setValue(order).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Order Confirmed")
                builder.setMessage("Your order was confirmed, we will be back to you soon.")
                builder.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
                builder.show()

                usersRef.child(auth.uid!!).child("cart").setValue(emptyList<String>())

            } else {
                val exception = task.exception
                Toast.makeText(this, "Failed to place order: ${exception?.message}", Toast.LENGTH_LONG).show()
            }

            progressBar.visibility = View.GONE
            confirmBtn.visibility = View.VISIBLE
        }
    }
}