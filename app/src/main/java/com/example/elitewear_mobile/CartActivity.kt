package com.example.elitewear_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.CartAdapter
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.CartItem

class CartActivity : AppCompatActivity() {
    private lateinit var cartListView: ListView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var backToProductsButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cartAdapter: CartAdapter

    companion object {
        val globalCartItems = mutableListOf<CartItem>()
    }

    private val paymentActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            cartAdapter.createOrderAfterPayment(true)
            Toast.makeText(this, "Order created successfully!", Toast.LENGTH_SHORT).show()

            globalCartItems.clear()
            cartAdapter.notifyDataSetChanged()  // Refresh the ListView
            updateTotalPrice()

            // Trigger cart deletion after payment success
            deleteCartAfterPayment()

            // Redirect to product list
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Payment failed or canceled. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userID = sharedPreferences.getInt("userId", 0)

        // Initialize views
        cartListView = findViewById(R.id.cartListView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)
        backToProductsButton = findViewById(R.id.backToProductsButton)

        // Back to products button action
        backToProductsButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        // Initialize adapter
        cartAdapter = CartAdapter(this, globalCartItems, ::updateTotalPrice, userID)
        cartListView.adapter = cartAdapter

        // Fetch cart items from API
        ApiClient.fetchCartItems(userID) { fetchedCartItems, totalPrice ->
            runOnUiThread {
                globalCartItems.clear()
                globalCartItems.addAll(fetchedCartItems)
                cartAdapter.notifyDataSetChanged()
                totalPriceTextView.text = "Total Price: $${String.format("%.2f", totalPrice)}"
            }
        }

        // Checkout button action
        checkoutButton.setOnClickListener {
            val totalPrice = globalCartItems.sumOf { it.price * it.quantity }
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("TOTAL_PRICE", totalPrice)
            }
            paymentActivityLauncher.launch(intent)
        }

        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val totalPrice = globalCartItems.sumOf { it.price * it.quantity }
        totalPriceTextView.text = "Total Price: $${String.format("%.2f", totalPrice)}"
    }

    // Function to delete the cart after payment success
    private fun deleteCartAfterPayment() {
        val userID = sharedPreferences.getInt("userId", 0)
        ApiClient.deleteCart(userID) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Cart deleted successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete cart.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
