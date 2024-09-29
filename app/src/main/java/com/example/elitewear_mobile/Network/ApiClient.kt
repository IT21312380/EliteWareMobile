package com.example.elitewear_mobile.Network


import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.models.Product
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private val client = OkHttpClient()

    fun fetchCartItems(callback: (List<CartItem>, Double) -> Unit) {
        val url = "https://localhost:5133/api/cart"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonResponse = JSONArray(responseBody.string())
                    val cartItems = mutableListOf<CartItem>()
                    var totalPrice = 0.0

                    for (i in 0 until jsonResponse.length()) {
                        val jsonCart = jsonResponse.getJSONObject(i)
                        val itemsArray = jsonCart.getJSONArray("items")

                        for (j in 0 until itemsArray.length()) {
                            val jsonItem = itemsArray.getJSONObject(j)
                            val name = jsonItem.getString("name")
                            val price = jsonItem.getDouble("price")
                            val quantity = jsonItem.getInt("quantity")

                            cartItems.add(CartItem(name, price, quantity))
                            totalPrice += price * quantity
                        }
                    }

                    callback(cartItems, totalPrice)
                }
            }
        })
    }

    fun fetchProducts(callback: (List<Product>) -> Unit) {

        val url = "http://10.0.2.2:7164/api/product"

        //val url = "https://datausa.io/api/data?drilldowns=Nation&measures=Population"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val rawResponse = responseBody.string() // Store the string once
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
                        val imageUrl = jsonProduct.optString("imageUrl", null)

                        products.add(Product(id, name, description, price, category, quantity, vendorId, imageUrl))
                    }

                    // Pass the products to the callback (UI handling is done in the Activity)
                    callback(products)
                }
            }
        })
    }




}