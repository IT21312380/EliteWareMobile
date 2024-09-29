package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.ProductAdapter
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.Product

class ProductListActivity : AppCompatActivity() {

    private lateinit var productListView: ListView
    private lateinit var productAdapter: ProductAdapter
    private val products = mutableListOf<Product>() // Store products for later use

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        productListView = findViewById(R.id.productListView)

        // Initialize adapter with empty list for now
        productAdapter = ProductAdapter(this, products)
        productListView.adapter = productAdapter

        // Set the add to cart click listener
        productAdapter.addToCartClickListener = object : ProductAdapter.OnAddToCartClickListener {
            override fun onAddToCartClick(product: Product) {
                addToCart(product) // This line calls your local addToCart function
            }
        }

        val viewCartButton = findViewById<Button>(R.id.viewCartButton)
        viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Fetch products from the API
        ApiClient.fetchProducts { fetchedProducts ->
            runOnUiThread {
                products.clear()
                products.addAll(fetchedProducts)
                productAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addToCart(product: Product) {
        // Assuming quantity is 1 for simplicity
        val cartItem = mapOf(
            "name" to product.name,
            "price" to product.price,
            "quantity" to 1
        )

        val cartData = mapOf(
            "id" to product.id, // or generate a unique cart ID
            "userId" to 0, // Replace with actual user ID if available
            "items" to listOf(cartItem),
            "totalPrice" to product.price
        )

        ApiClient.addToCart(cartData) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
