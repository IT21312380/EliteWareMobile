package com.example.elitewear_mobile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Network.ApiClient2
import com.example.elitewear_mobile.models.Review

class EditReviewActivity : AppCompatActivity() {

    private lateinit var review: Review // Ensure this is initialized after fetching the review
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var submitReviewButton: Button
    private lateinit var deleteReviewButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        ratingBar = findViewById(R.id.ratingBar)
        submitReviewButton = findViewById(R.id.submitReviewButton)
        deleteReviewButton = findViewById(R.id.deleteReviewButton)

        // Get the review ID from the intent
        val reviewId = intent.getIntExtra("REVIEW_ID", -1)
        Log.d("EditReviewActivity", "Received Review ID: $reviewId")

        nameEditText.isEnabled = false  // Disable editing
        //nameEditText.isFocusable = false // Prevent the field from being focused
        //nameEditText.isCursorVisible = false // Hide the cursor

        if (reviewId != -1) {
            // Fetch the review from the API
            ApiClient2.fetchSingleReview(reviewId) { fetchedReview ->
                runOnUiThread {
                    fetchedReview?.let {
                        review = it // Initialize the review object with fetched data
                        nameEditText.setText(it.name)
                        descriptionEditText.setText(it.description)
                        ratingBar.rating = it.rate.toFloat()
                    }
                }
            }
        }

        deleteReviewButton.setOnClickListener {
            Log.d("EditReviewActivity", "Delete button clicked")

            // Call the API to delete the review
            ApiClient2.deleteReview(reviewId) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Review deleted successfully", Toast.LENGTH_SHORT).show()
                        finish()  // Close the activity after successful deletion
                    } else {
                        Toast.makeText(this, "Failed to delete review", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



        submitReviewButton.setOnClickListener {
            Log.d("EditReviewActivity", "Submit button clicked")

            val updatedName = nameEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()
            val updatedRating = ratingBar.rating.toInt()

            Log.d("EditReviewActivity", "Updated data: Name: $updatedName, Description: $updatedDescription, Rating: $updatedRating")

            if (updatedName.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Ensure that the review object is initialized before updating
                if (!::review.isInitialized) {
                    Log.e("EditReviewActivity", "Review object not initialized")
                    Toast.makeText(this, "Error: Review data not loaded properly", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Create an updated review object
                val updatedReview = Review(
                    id = reviewId,  // Use the same review ID
                    vendorID = review.vendorID,  // Use the captured vendor ID
                    name = updatedName,
                    description = updatedDescription,
                    rate = updatedRating
                )

                Log.d("EditReviewActivity", "Attempting to update review: $updatedReview")

                // Call the API to update the review
                ApiClient2.updateReview(updatedReview) { success ->
                    runOnUiThread {
                        Log.d("EditReviewActivity", "API response: $success")
                        if (success) {
                            Toast.makeText(this, "Review updated successfully", Toast.LENGTH_SHORT).show()
                            finish()  // Close the activity after successful update
                        } else {
                            Toast.makeText(this, "Failed to update review", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



    }
}
