package com.example.elitewear_mobile

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.CartAdapter
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.R

class CartActivity : AppCompatActivity() {
    private lateinit var cartListView: ListView
    private lateinit var totalPriceTextView: TextView
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartListView = findViewById(R.id.cartListView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        cartAdapter = CartAdapter(this, cartItems)
        cartListView.adapter = cartAdapter

        // Fetch and load cart items from API
        ApiClient.fetchCartItems { fetchedCartItems, totalPrice ->
            runOnUiThread {
                cartItems.clear()
                cartItems.addAll(fetchedCartItems)
                cartAdapter.notifyDataSetChanged()
                totalPriceTextView.text = "Total Price: $$totalPrice"
            }
        }
    }
}