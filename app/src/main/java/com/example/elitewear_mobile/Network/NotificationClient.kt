package com.example.elitewear_mobile.Network

import com.example.elitewear_mobile.models.Notification

import okhttp3.Call
import okhttp3.Callback

import okhttp3.OkHttpClient
import okhttp3.Request

import okhttp3.Response
import org.json.JSONArray

import java.io.IOException

object NotificationClient {
    private val client = OkHttpClient()

    fun fetchNotifications(userId: Int,callback: (List<Notification>) -> Unit) {
        val url = "http://10.0.2.2:5133/api/Notification/csr/$userId"  // Updated to 10.0.2.2

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val rawResponse = responseBody.string()  // Store the string once
                    println("Response from API: $rawResponse")
                    val jsonResponse = JSONArray(rawResponse)
                    val notifications = mutableListOf<Notification>()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonNotification = jsonResponse.getJSONObject(i)
                        val id = jsonNotification.getInt("id")
                        val message = jsonNotification.getString("message")
                        val customerId = jsonNotification.getInt("customerId")
                        val notificationType = jsonNotification.getString("notificationType")
                        val createdAt = jsonNotification.getString("createdAt")


                        notifications.add(
                            Notification(
                                id,
                                message,
                                customerId,
                                notificationType,
                                createdAt

                            )
                        )
                    }

                    // Pass the products to the callback (UI handling is done in the Activity)
                    callback(notifications)
                }
            }


        })
    }
    }

