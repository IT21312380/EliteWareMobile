package com.example.elitewear_mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.ProductAdapter
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.models.Product

class ProductListActivity : AppCompatActivity() {

    private lateinit var productListView: ListView
    private lateinit var productAdapter: ProductAdapter
    private val products = mutableListOf<Product>() // Store products for later use

    @SuppressLint("MissingInflatedId")
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
                addToCart(product) // Call the addToCart function
            }
        }

        val viewCartButton = findViewById<Button>(R.id.viewCartButton)
        viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        val viewOrderButton = findViewById<Button>(R.id.viewOrderButton)
        viewOrderButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
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

    // Function to add a product to the cart
    private fun addToCart(product: Product) {
        // Use globalCartItems from CartActivity
        val existingCartItem = CartActivity.globalCartItems.find { it.id == product.id }

        if (existingCartItem != null) {
            // Product is already in the cart, increment its quantity
            existingCartItem.quantity++
            println("Updated quantity for ${existingCartItem.name} to ${existingCartItem.quantity}")
        } else {
            // Add a new product to the cart
            val newCartItem = CartItem(
                id = product.id,
                name = product.name,
                imageURL = product.imageUrl,
                price = product.price,
                quantity = 1
            )
            CartActivity.globalCartItems.add(newCartItem)
            println("Added new item to cart: ${newCartItem.name}")
        }

        // Debugging: Log the current cart items
        println("Current global cart items: ${CartActivity.globalCartItems}")

        // Check if cart ID 12 exists
        val cartId = 12 // Fixed cart ID

        // Prepare the cart data to be sent to the server
        val cartData = mapOf(
            "id" to cartId, // Use cart ID 12
            "userId" to 0, // Replace with actual user ID if available
            "items" to CartActivity.globalCartItems.map { cartItem ->
                mapOf(
                    "id" to cartItem.id,
                    "name" to cartItem.name,
                    "imageURL" to cartItem.imageURL,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity
                )
            },
            "totalPrice" to CartActivity.globalCartItems.sumOf { it.price * it.quantity }
        )

        // Send cart data to the server
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
