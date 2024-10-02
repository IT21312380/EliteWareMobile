package com.example.elitewear_mobile.models

data class Payment(
    val id: Int = 0,
    val cardType: String,
    val amount: Double,
    val billingAddress: String,
    val expireDate: String
)