package com.example.e_commerce.utils

import android.content.Context
import android.text.TextUtils.replace
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.e_commerce.R

object Util {

    fun showAlertDialog(context: Context, title: String, message: String, negativeButtonMessage: String = "Yes", operation: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Yes") { _, _ ->
            operation?.invoke()
        }
        builder.setNegativeButton(negativeButtonMessage) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun setSpinner(spinner: Spinner, context: Context) {

        ArrayAdapter.createFromResource(
            context,
            R.array.product_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
        }
    }
}
