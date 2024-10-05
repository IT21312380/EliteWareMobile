package com.example.elitewear_mobile

import android.os.Bundle
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        notificationListView = findViewById(R.id.notificationListView)

        // Initialize adapter with empty list for now
        notificationAdapter = NotificationAdapter(this,notifications)
        notificationListView.adapter = notificationAdapter

        // Fetch products from the API
        NotificationClient.fetchNotifications { fetchedNotifications ->
            runOnUiThread {
                notifications.clear()
                notifications.addAll(fetchedNotifications)
                notificationAdapter.notifyDataSetChanged()
            }
        }

    }
}