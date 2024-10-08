package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.Network.ApiClient2
import com.example.elitewear_mobile.models.Review

class AddReviewActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        // Initialize the views
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        ratingBar = findViewById(R.id.ratingBar)
        submitButton = findViewById(R.id.submitReviewButton)

        val vendorId = intent.getIntExtra("vendorId", 1234) // Pass vendorId to the intent

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")

        nameEditText.setText(username)
        nameEditText.isEnabled = false  // Disable editing
        nameEditText.isFocusable = false // Prevent the field from being focused
        nameEditText.isCursorVisible = false // Hide the cursor

        // Handle submit button click
        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val rating = ratingBar.rating.toInt()

            val newReview = Review(0, vendorId, name, description, rating)

            // Use ApiClient to submit the review (Create this API method in the client)
            ApiClient2.addReview(newReview) {
                val intent = Intent(this, com.example.elitewear_mobile.Review::class.java)
                intent.putExtra("vendorId", vendorId)
                startActivity(intent)
                finish()
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