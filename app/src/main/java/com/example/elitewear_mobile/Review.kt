package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.Adapters.ReviewAdapter
import com.example.elitewear_mobile.Network.ApiClient2

class Review : AppCompatActivity() {

    private lateinit var reviewListView : ListView
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var averageRatingTextView: TextView
    private lateinit var addReviewButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reviewListView = findViewById(R.id.reviewListView)
        averageRatingTextView = findViewById(R.id.averageRatingTextView)
        addReviewButton = findViewById(R.id.addReviewButton)

        val vendorId = intent.getIntExtra("vendorId", -1)

        if (vendorId != -1) {
            // Fetch reviews for the given vendorId
            ApiClient2.fetchReviews(vendorId) { reviews ->
                runOnUiThread {
                    reviewAdapter = ReviewAdapter(this, reviews)
                    reviewListView.adapter = reviewAdapter

                    val averageRating = reviews.map { it.rate }.average()
                    averageRatingTextView.text = "Average Rating: %.2f".format(averageRating)
                }
            }
        }

        addReviewButton.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("vendorId", vendorId)
            startActivity(intent)
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