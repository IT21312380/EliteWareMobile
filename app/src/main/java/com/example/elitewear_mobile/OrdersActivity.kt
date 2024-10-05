package com.example.elitewear_mobile

import android.os.Bundle
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
    private val userId = 13  // Replace this with the specific userId you are fetching orders for

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Initialize ListView and Adapter
        ordersListView = findViewById(R.id.ordersListView)
        orderAdapter = OrderAdapter(this, ordersList)
        ordersListView.adapter = orderAdapter

        // Fetch the orders for a specific user
        fetchOrdersForUser(userId)
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
