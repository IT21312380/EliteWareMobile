package com.example.elitewear_mobile.models

data class Product (
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val quantity: Int,
    val vendorId: Int,
    val imageUrl: String
)