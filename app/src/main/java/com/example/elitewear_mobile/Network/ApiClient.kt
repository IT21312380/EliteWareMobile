package com.example.elitewear_mobile.Network


import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.models.Product
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private val client = OkHttpClient()

    fun fetchCartItems(callback: (List<CartItem>, Double) -> Unit) {
        val url = "http://10.0.2.2:5133/api/cart"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Ensure we read the body only once
                val rawResponse = response.body?.string() ?: return
                println("Response from API: $rawResponse")
                val jsonResponse = JSONArray(rawResponse)
                val cartItems = mutableListOf<CartItem>()
                var totalPrice = 0.0

                // Iterate through each cart object in the response
                for (i in 0 until jsonResponse.length()) {
                    val jsonCart = jsonResponse.getJSONObject(i)
                    val itemsArray = jsonCart.getJSONArray("items")

                    // Iterate through each item in the cart
                    for (j in 0 until itemsArray.length()) {
                        val jsonItem = itemsArray.getJSONObject(j)
                        val id =jsonItem.getInt("id")
                        val name = jsonItem.getString("name")
                        val price = jsonItem.getDouble("price")
                        val quantity = jsonItem.getInt("quantity")
                        var imageURL = jsonItem.optString("imageURL", "")
                        if (imageURL.isNotEmpty()) {
                            imageURL = imageURL.replace("localhost", "10.0.2.2")
                        }

                        cartItems.add(CartItem(id,name,imageURL, price, quantity))
                        println("image from API: $cartItems")
                        totalPrice += price * quantity
                    }
                }

                // Update the UI with the items and total price
                callback(cartItems, totalPrice)
            }
        })
    }



    fun fetchProducts(callback: (List<Product>) -> Unit) {
        val url = "http://10.0.2.2:5133/api/product"  // Updated to 10.0.2.2

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
                    val products = mutableListOf<Product>()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonProduct = jsonResponse.getJSONObject(i)
                        val id = jsonProduct.getInt("id")
                        val name = jsonProduct.getString("name")
                        val description = jsonProduct.getString("description")
                        val price = jsonProduct.getDouble("price")
                        val category = jsonProduct.getString("category")
                        val quantity = jsonProduct.getInt("quantity")
                        val vendorId = jsonProduct.getInt("vendorId")

                        // Get the image URL and replace 'localhost' with '10.0.2.2'
                        var imageUrl = jsonProduct.optString("imageUrl", null)
                        imageUrl = imageUrl?.replace("localhost", "10.0.2.2")
                        products.add(Product(id, name, description, price, category, quantity, vendorId, imageUrl))
                    }

                    // Pass the products to the callback (UI handling is done in the Activity)
                    callback(products)
                }
            }


        })
    }
    fun addToCart(cartData: Map<String, Any>, callback: (Boolean) -> Unit) {
        // Assuming your cart ID is included in the cartData map
        val cartId = cartData["id"] as? Int ?: return callback(false) // Ensure cart ID is available
        val url = "http://10.0.2.2:5133/api/cart/$cartId" // Endpoint for updating cart

        // Determine whether to update or create a new cart
        val requestMethod = if (cartId == 12) "PUT" else "POST" // Change condition based on your logic
        val jsonBody = JSONObject(cartData).toString()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")

        // Use PUT for updating, POST for creating
        if (requestMethod == "PUT") {
            requestBuilder.put(requestBody) // Use PUT to update existing cart
        } else {
            requestBuilder.post(requestBody) // Use POST to create a new cart
        }

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // Return false on failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseString = responseBody.string() // Read response body
                    println("Response from API (Add to Cart): $responseString")

                    // Check if the response was successful
                    if (response.isSuccessful) {
                        // You might want to parse the response here if needed
                        // For example, check for a success message or a cart ID
                        callback(true) // Indicate success
                    } else {
                        println("Error: ${response.code} - ${response.message}")
                        callback(false) // Indicate failure
                    }
                } ?: run {
                    println("Error: Response body is null.")
                    callback(false) // Indicate failure
                }
            }
        })
    }
    fun removeCartItem(cartId: Int, itemId: Int, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:5133/api/cart/$cartId/items/$itemId"  // Update with correct localhost IP

        // Create a DELETE request
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        // Make the API call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // Return failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true) // Return success
                } else {
                    callback(false) // Return failure
                }
            }
        })
    }

}