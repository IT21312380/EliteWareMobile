package com.example.elitewear_mobile.Network


import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.models.Order
import com.example.elitewear_mobile.models.OrderItem
import com.example.elitewear_mobile.models.Payment
import com.example.elitewear_mobile.models.Product
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private val client = OkHttpClient()

    fun fetchCartItems(cartId: Int, callback: (List<CartItem>, Double) -> Unit) {
        val url = "http://10.0.2.2:5133/api/cart/$cartId" // Fetch a specific cart by its ID
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val rawResponse = response.body?.string() ?: return
                println("Response from API: $rawResponse")

                try {
                    val jsonResponse = JSONObject(rawResponse) // Expect a single cart object
                    if (jsonResponse.has("items")) {
                        val itemsArray = jsonResponse.getJSONArray("items") // The cart items array
                        val cartItems = mutableListOf<CartItem>()
                        var totalPrice = 0.0

                        if (itemsArray.length() == 0) {
                            // No items in the cart
                            println("No items in the cart.")
                            callback(emptyList(), 0.0)
                            return
                        }

                        // Iterate through each item in the cart
                        for (j in 0 until itemsArray.length()) {
                            val jsonItem = itemsArray.getJSONObject(j)
                            val id = jsonItem.getInt("id")
                            val name = jsonItem.getString("name")
                            val price = jsonItem.getDouble("price")
                            val quantity = jsonItem.getInt("quantity")
                            var imageURL = jsonItem.optString("imageURL", "")

                            if (imageURL.isNotEmpty()) {
                                imageURL = imageURL.replace("localhost", "10.0.2.2")
                            }

                            cartItems.add(CartItem(id, name, imageURL, price, quantity))
                            totalPrice += price * quantity
                        }

                        // Return the fetched items and total price
                        callback(cartItems, totalPrice)
                    } else {
                        // The 'items' field is missing, which means no items in the cart
                        println("No items in the cart.")
                        callback(emptyList(), 0.0)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    println("Error parsing JSON response.")
                    // In case of a parsing error, return empty cart
                    callback(emptyList(), 0.0)
                }
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
                        val status = jsonProduct.getString("status")  // Retrieve the status
                        var imageUrl = jsonProduct.optString("imageUrl", null)
                        imageUrl = imageUrl?.replace("localhost", "10.0.2.2")
                        // Only include products with "Active" status
                        if (status == "Active") {
                            products.add(Product(id, name, description, price, category, quantity, vendorId, imageUrl))
                        }
                    }

                    // Pass the active products to the callback (UI handling is done in the Activity)
                    callback(products)
                }
            }
        })
    }

    fun addToCart(cartData: Map<String, Any>, callback: (Boolean) -> Unit) {
        // Check if the cart ID is provided
        val cartId = cartData["userId"] as? Int
        println(cartId)

        val url = if (cartId != null) {
            "http://10.0.2.2:5133/api/cart/$cartId" // Update existing cart
        } else {
            "http://10.0.2.2:5133/api/cart" // Create a new cart
        }

        val requestMethod = if (cartId != null) "PUT" else "POST" // Use PUT for update, POST for new cart

        val jsonBody = JSONObject(cartData).toString()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")

        // Select the correct HTTP method (POST or PUT)
        if (requestMethod == "PUT") {
            requestBuilder.put(requestBody) // Updating existing cart
        } else {
            requestBuilder.post(requestBody) // Creating a new cart
        }

        val request = requestBuilder.build()
       println(request)
        println(jsonBody)
        println(requestBody)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // Failure callback
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseString = response.body?.string()
                    println("Response from API (Add to Cart): $responseString")

                    // You might want to parse the response to confirm cart updates were successful
                    callback(true) // Success callback
                } else {
                    println("Failed to add to cart: ${response.code} - ${response.message}")
                    callback(false) // Failure callback
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
    fun addPayment(payment: Payment, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:5133/api/payment"
        val jsonBody = JSONObject()
            .put("cardType", payment.cardType)
            .put("amount", payment.amount)
            .put("billingAddress", payment.billingAddress)
            .put("expireDate", payment.expireDate)
            .toString()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val responseString = requestBody// Read response body
        println("order req API : $responseString")
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }
    fun createOrder(orderData: Map<String, Any>, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:5133/api/order"  // Adjust the URL based on your localhost setup

        val jsonBody = JSONObject(orderData).toString()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // Indicate failure
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    println("Order created successfully.")
                    callback(true) // Indicate success
                } else {
                    println("Failed to create order: ${response.code} - ${response.message}")
                    callback(false) // Indicate failure
                }
            }
        })
    }
    fun fetchOrdersByUserId(userId: Int, callback: (List<Order>) -> Unit) {
        val url = "http://10.0.2.2:5133/api/order/$userId"  // Specific userId for filtering orders

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(emptyList()) // Return an empty list on failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val rawResponse = responseBody.string()
                    println("Response from API: $rawResponse")
                    val jsonResponse = JSONArray(rawResponse)
                    val orders = mutableListOf<Order>()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonOrder = jsonResponse.getJSONObject(i)
                        val id = jsonOrder.getInt("id")
                        val totalPrice = jsonOrder.getDouble("totalPrice")
                        val status = jsonOrder.getString("status")

                        val itemsArray = jsonOrder.getJSONArray("items")
                        val orderItems = mutableListOf<OrderItem>()

                        for (j in 0 until itemsArray.length()) {
                            val jsonItem = itemsArray.getJSONObject(j)
                            val itemId = jsonItem.getInt("id")
                            val itemName = jsonItem.getString("name")
                            val itemQty = jsonItem.getInt("qty")
                            val itemStatus = jsonItem.getString("status")
                            orderItems.add(OrderItem(itemId, itemName, itemQty, itemStatus))
                        }

                        orders.add(Order(id, userId, orderItems, totalPrice, status))
                    }

                    callback(orders)
                } ?: run {
                    println("Error: Response body is null.")
                    callback(emptyList()) // Return an empty list if the response body is null
                }
            }
        })
    }
    fun checkUserCart(userId: Int, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:5133/api/cart/$userId" // Adjust URL if necessary

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "*/*")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // User cart check failed
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // If the response is successful, the user cart exists
                    callback(true)
                } else {
                    // If response is not successful, user cart does not exist
                    callback(false)
                }
            }
        })
    }
    fun createCart(cartData: Map<String, Any>, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:5133/api/cart" // Adjust to your create cart endpoint
        val jsonBody = JSONObject(cartData).toString()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false) // Cart creation failed
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful) // Return success status
            }
        })
    }
    fun fetchProductById(url: String, callback: (Product) -> Unit) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val rawResponse = responseBody.string()
                    println("Response from API: $rawResponse")
                    val jsonProduct = JSONObject(rawResponse)

                    val id = jsonProduct.getInt("id")
                    val name = jsonProduct.getString("name")
                    val description = jsonProduct.getString("description")
                    val price = jsonProduct.getDouble("price")
                    val category = jsonProduct.getString("category")
                    val quantity = jsonProduct.getInt("quantity")
                    val vendorId = jsonProduct.getInt("vendorId")
                    //val imageUrl = jsonProduct.optString("imageUrl", null)?.replace("localhost", "10.0.2.2")
                    var imageUrl = jsonProduct.optString("imageUrl", null)
                    imageUrl = imageUrl?.replace("localhost", "10.0.2.2")

                    val product = Product(id, name, description, price, category, quantity, vendorId, imageUrl)
                    callback(product)
                }
            }
        })
    }
    fun deleteCart(userId: Int, callback: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("http://10.0.2.2:5133/api/cart/$userId")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }

}