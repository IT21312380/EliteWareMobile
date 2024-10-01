package com.example.elitewear_mobile.models

data class Review (
    val id:Int,
    val vendorID:Int,
    val name: String,
    val description: String,
    val rate:Int
)