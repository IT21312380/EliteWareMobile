package com.example.elitewear_mobile.models

data class Cart (
    val id: Int,
    val userId: Int,
    val items: List<CartItem>,
    val totalPrice: Double
)
