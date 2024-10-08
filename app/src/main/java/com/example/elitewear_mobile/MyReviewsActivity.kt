package com.example.elitewear_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.ReviewAdapter
import com.example.elitewear_mobile.Network.ApiClient2

class MyReviewsActivity : AppCompatActivity() {

    private lateinit var myReviewsListView: ListView
    private lateinit var myReviewsAdapter: ReviewAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reviews)

        myReviewsListView = findViewById(R.id.myReviewsListView)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

        if (!username.isNullOrEmpty()) {
            ApiClient2.getReviewsForUser(username) { reviews ->
                runOnUiThread {
                    myReviewsAdapter = ReviewAdapter(this, reviews)
                    myReviewsListView.adapter = myReviewsAdapter
                }
            }
        } else {
            println("No user is currently logged in")
        }

        // Set up navigation (similar to your previous activities)

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
