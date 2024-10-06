package com.example.elitewear_mobile.models

data class UpdateUser (



    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val state: String,
    val requested: String

)