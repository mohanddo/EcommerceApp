package com.example.e_commerce.models

data class User(val name: String = "",
                val phoneNumber: String = "",
    val email: String = "",
    val cart: MutableList<String> = mutableListOf(),
    val profilePic: String? = null
)
