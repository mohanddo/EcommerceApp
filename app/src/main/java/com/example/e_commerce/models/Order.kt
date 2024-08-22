package com.example.e_commerce.models

data class Order(val uid: String = "",
                 val orderId: String = "",
                 val name: String = "",
                 val phoneNumber: String = "",
                 val wilaya: String = "",
                 val commun: String = "",
                 val address: String = "",
                 val typeShippment: String = "",
                 val comment: String = "",
                 val totalPrice: String = "",
                 val productsNames: List<String> = emptyList(),
                 val state: String = ""
)
