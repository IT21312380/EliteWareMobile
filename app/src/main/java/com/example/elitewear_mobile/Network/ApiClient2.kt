package com.example.elitewear_mobile.Network

import android.widget.Toast
import com.example.elitewear_mobile.models.Review
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

object ApiClient2 {

    private val client = OkHttpClient()
    fun fetchReviews(vendorID: Int, callback: (List<Review>)-> Unit){

        val url = "http://10.0.2.2:5133/api/review/vendor/$vendorID"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            //add toast message if needed

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val rawResponse = responseBody.string() // St2ore the string once
                    println("Response from API: $rawResponse")
                    val jsonResponse = JSONArray(rawResponse)
                    val reviews = mutableListOf<Review>()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonReview = jsonResponse.getJSONObject(i)
                        val id = jsonReview.getInt("id")
                        val name = jsonReview.getString("name")
                        val vendorID = jsonReview.getInt("vendorID")
                        val description = jsonReview.getString("description")
                        val rate = jsonReview.getInt("rate")


                        reviews.add(Review(id,vendorID,name,description,rate))
                    }

                    // Pass the products to the callback (UI handling is done in the Activity)
                    callback(reviews)
                }
            }
        })
    }
}