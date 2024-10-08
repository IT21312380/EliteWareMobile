package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.Adapters.NotificationAdapter
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.Network.NotificationClient
import com.example.elitewear_mobile.models.Notification

class NotificationsActivity : AppCompatActivity() {


    private lateinit var notificationListView: ListView
    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        notificationListView = findViewById(R.id.notificationListView)

        // Initialize adapter with empty list for now
        notificationAdapter = NotificationAdapter(this,notifications)
        notificationListView.adapter = notificationAdapter

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId=sharedPreferences.getInt("userId",0) ?:""
        Log.d("LoginActivity", "User ID: $userId")
        // Fetch products from the API
        NotificationClient.fetchNotifications(userId as Int)  { fetchedNotifications ->
            runOnUiThread {
                notifications.clear()
                notifications.addAll(fetchedNotifications)
                notificationAdapter.notifyDataSetChanged()
            }
        }

        val HomeButton = findViewById<ImageView>(R.id.navHomeUnClick)
        val ProfilePageButton = findViewById<ImageView>(R.id.navProfileUnClick)
        val CartPageButton = findViewById<ImageView>(R.id.navCartUnClick)
        val NotifyPageButton = findViewById<ImageView>(R.id.navNotifyUnClick)
        val OrderHistoryButton = findViewById<ImageView>(R.id.navOrderHistoryUnClick)

        OrderHistoryButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        HomeButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
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

    }
}