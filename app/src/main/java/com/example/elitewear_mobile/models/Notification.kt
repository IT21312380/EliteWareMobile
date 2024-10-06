package com.example.elitewear_mobile.models

import java.util.Date

data class Notification (
    val id: Int,
    val Message: String,
    val CustomerId:Int,
    val NotificationType:String,
    val createdAt: String
)