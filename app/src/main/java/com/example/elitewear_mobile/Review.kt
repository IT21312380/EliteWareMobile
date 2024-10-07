package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        enableEdgeToEdge()
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}