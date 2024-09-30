package com.example.elitewear_mobile.models

data class CartItem (
    val id: Int,
    val name: String,
    val imageURL: String,
    val price: Double,
    var quantity: Int
)