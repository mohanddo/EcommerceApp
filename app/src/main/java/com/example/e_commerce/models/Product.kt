package com.example.e_commerce.models

data class Product(val uid: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val type: String = "",
    val imageLink: String = ""
)
