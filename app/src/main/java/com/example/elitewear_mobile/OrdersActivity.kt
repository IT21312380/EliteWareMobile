package com.example.elitewear_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.models.Order
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.Adapters.OrderAdapter

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersListView: ListView
    private lateinit var orderAdapter: OrderAdapter
    private val ordersList: MutableList<Order> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var userId = 0  // Replace this with the specific userId you are fetching orders for

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userID = sharedPreferences.getInt("userId", 0)
        userId=userID
        // Initialize ListView and Adapter
        ordersListView = findViewById(R.id.ordersListView)
        orderAdapter = OrderAdapter(this, ordersList)
        ordersListView.adapter = orderAdapter

        // Fetch the orders for a specific user
        fetchOrdersForUser(userId)
        val ReviewPageButton = findViewById<ImageView>(R.id.navReviewUnClick)
        val HomeButton = findViewById<ImageView>(R.id.navHomeUnClick)
        val ProfilePageButton = findViewById<ImageView>(R.id.navProfileUnClick)
        val CartPageButton = findViewById<ImageView>(R.id.navCartUnClick)
        val NotifyPageButton = findViewById<ImageView>(R.id.navNotifyUnClick)
        val OrderHistoryButton = findViewById<ImageView>(R.id.navOrderHistoryUnClick) // New Order History Button

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

// New Order History Click Listener
        OrderHistoryButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchOrdersForUser(userId: Int) {
        ApiClient.fetchOrdersByUserId(userId) { orders ->
            runOnUiThread { // Ensure UI updates happen on the main thread
                if (orders.isNotEmpty()) {
                    ordersList.clear()
                    ordersList.addAll(orders)
                    orderAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "No orders found for user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
