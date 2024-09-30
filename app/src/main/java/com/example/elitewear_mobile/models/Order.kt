package com.example.elitewear_mobile.models

data class Order(
    val id: Int,
    val userId: Int,
    val items: List<OrderItem>, // List of items from the cart
    val totalPrice: Double = 0.0,
    val status: String = "Initiated" // Initial status
)