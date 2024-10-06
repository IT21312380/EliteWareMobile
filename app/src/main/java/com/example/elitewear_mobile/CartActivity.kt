package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.CartAdapter
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.Network.ApiClient

class CartActivity : AppCompatActivity() {
    private lateinit var cartListView: ListView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var backToProductsButton: Button

    // Static cart items to maintain across activities
    companion object {
        val globalCartItems = mutableListOf<CartItem>()
    }

    private lateinit var cartAdapter: CartAdapter

    // Launcher to handle the result from PaymentActivity
    private val paymentActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Payment successful
            cartAdapter.createOrderAfterPayment(true)
            Toast.makeText(this, "Order created successfully!", Toast.LENGTH_SHORT).show()

            globalCartItems.clear()
            cartAdapter.notifyDataSetChanged()  // Refresh the ListView
            updateTotalPrice()
            val intent = Intent(this, ProductListActivity::class.java)  // Change to your actual product list activity class
            startActivity(intent)

        } else {
            // Payment failed or was canceled
            Toast.makeText(this, "Payment failed or canceled. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val ReviewPageButton = findViewById<ImageView>(R.id.navReviewUnClick)
        val HomeButton = findViewById<ImageView>(R.id.navHomeUnClick)
        val ProfilePageButton = findViewById<ImageView>(R.id.navProfileUnClick)
        val CartPageButton = findViewById<ImageView>(R.id.navCartUnClick)
        val NotifyPageButton = findViewById<ImageView>(R.id.navNotifyUnClick)

        HomeButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }
        ReviewPageButton.setOnClickListener {
            val intent = Intent(this, MyReviewsActivity::class.java)
            startActivity(intent)
        }
        ProfilePageButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        CartPageButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        NotifyPageButton.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        // Initialize views
        cartListView = findViewById(R.id.cartListView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)
        backToProductsButton = findViewById(R.id.backToProductsButton)  // Initialize the button

        // Set up back to products button click listener
        backToProductsButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)  // Adjust the class name if different
            startActivity(intent)  // Start the ProductListActivity
        }

        // Initialize the adapter with the global cart items
        cartAdapter = CartAdapter(this, globalCartItems) {
            updateTotalPrice() // Callback for updating total price when quantity changes
        }
        cartListView.adapter = cartAdapter
        val cartId = 13

            // Fetch and load cart items from API for the specific cart
            ApiClient.fetchCartItems(cartId) { fetchedCartItems, totalPrice ->
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
            val totalPrice = globalCartItems.sumOf { it.price * it.quantity }
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("TOTAL_PRICE", totalPrice) // Pass the total price
            }
            // Start PaymentActivity and wait for its result
            paymentActivityLauncher.launch(intent)
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
