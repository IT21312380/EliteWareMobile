package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.elitewear_mobile.models.Product
import com.example.elitewear_mobile.Network.ApiClient
import org.json.JSONObject
import java.io.IOException

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var productImageView: ImageView
    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var productDescriptionTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var vendorTextView: TextView
    private lateinit var vendorRevBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize views
        productImageView = findViewById(R.id.productImageView)
        productNameTextView = findViewById(R.id.productNameTextView)
        productPriceTextView = findViewById(R.id.productPriceTextView)
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView)
        quantityTextView = findViewById(R.id.quantityTextView)
        vendorTextView = findViewById(R.id.vendorTextView)
        vendorRevBtn = findViewById(R.id.vendorRevBtn)

        // Get the product ID passed from the previous activity
        val productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId != -1) {
            // Fetch product details from the API
            fetchProductDetails(productId)
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if product ID is not valid
        }
    }

    private fun fetchProductDetails(productId: Int) {
        val url = "http://10.0.2.2:5133/api/product/$productId"

        ApiClient.fetchProductById(url) { product ->
            runOnUiThread {
                // Update UI with product details
                productNameTextView.text = product.name
                productPriceTextView.text = "Price: $${product.price}"
                productDescriptionTextView.text = product.description
                quantityTextView.text = " ${product.quantity}"
                vendorTextView.text = " ${product.vendorId}"

                // Load product image using Glide
                Glide.with(this)
                    .load(product.imageUrl)
                    .into(productImageView)
            }
            vendorRevBtn.setOnClickListener {
                val intent = Intent(this, Review::class.java)
                intent.putExtra("vendorId", product.vendorId) // Pass vendorId to Review activity
                startActivity(intent)
            }
        }
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
}

