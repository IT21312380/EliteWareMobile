package com.example.elitewear_mobile

import android.os.Bundle
import android.widget.Button
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
    private lateinit var checkoutButton: Button

    // Static cart items to maintain across activities
    companion object {
        val globalCartItems = mutableListOf<CartItem>()
    }

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        cartListView = findViewById(R.id.cartListView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        // Initialize the adapter with the global cart items
        cartAdapter = CartAdapter(this, globalCartItems) {
            updateTotalPrice() // Callback for updating total price when quantity changes
        }
        cartListView.adapter = cartAdapter

        // Fetch and load cart items from API
        ApiClient.fetchCartItems { fetchedCartItems, totalPrice ->
            runOnUiThread {
                // Clear and add fetched items to the global cart items
                globalCartItems.clear()
                globalCartItems.addAll(fetchedCartItems)
                cartAdapter.notifyDataSetChanged()
                totalPriceTextView.text = "Total Price: $${String.format("%.2f", totalPrice)}"
            }
        }

        // Set up checkout button logic
        checkoutButton.setOnClickListener {
            // Handle checkout logic (e.g., API call to proceed with checkout)
        }

        // Update total price when activity starts
        updateTotalPrice()
    }

    // Function to calculate the total price
    private fun updateTotalPrice() {
        val totalPrice = globalCartItems.sumOf { it.price * it.quantity }
        totalPriceTextView.text = "Total Price: $${String.format("%.2f", totalPrice)}"
    }
}
