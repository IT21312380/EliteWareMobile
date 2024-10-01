package com.example.elitewear_mobile.models

data class OrderItem(
    val id: Int,
    val name: String,
    val qty: Int,
    val status: String? = null
)