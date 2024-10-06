package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.Adapters.ReviewAdapter
import com.example.elitewear_mobile.Network.ApiClient2

class MyReviewsActivity : AppCompatActivity() {

   // private lateinit var myReviewsListView: ListView
    //private lateinit var myReviewsAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_reviews)

    /*    myReviewsListView = findViewById(R.id.myReviewsListView)

        // Get the logged-in user's name from SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "") // Fetch stored userName

        // Fetch and filter reviews based on the logged-in user
        ApiClient2.fetchReviews { reviews ->
            val myReviews = reviews.filter { it.name == userName }
            runOnUiThread {
                myReviewsAdapter = ReviewAdapter(this, myReviews)
                myReviewsListView.adapter = myReviewsAdapter
            }
        }

        // Handle click events on reviews to edit
        myReviewsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedReview = myReviewsAdapter.getItem(position) as com.example.elitewear_mobile.models.Review

            // Navigate to EditReviewActivity with the selected review's data
            val intent = Intent(this, EditReviewActivity::class.java)
            intent.putExtra("reviewId", selectedReview.id)
            intent.putExtra("vendorId", selectedReview.vendorID)
            intent.putExtra("description", selectedReview.description)
            intent.putExtra("rate", selectedReview.rate)
            startActivity(intent)
        }
        
     */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}