package com.example.elitewear_mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Adapters.ProductAdapter
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.models.Product

class ProductListActivity : AppCompatActivity() {

    private lateinit var productGridView: GridView
    private lateinit var productAdapter: ProductAdapter
    private val products = mutableListOf<Product>() // Store products for later use
    private val filteredProducts = mutableListOf<Product>() // Store filtered products
    private lateinit var categorySpinner: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        productGridView = findViewById(R.id.productGridView)
        categorySpinner = findViewById(R.id.categorySpinner)

        // Initialize adapter with empty list for now
        productAdapter = ProductAdapter(this, filteredProducts)
        productGridView.adapter = productAdapter

        // Set the add to cart click listener
        productAdapter.addToCartClickListener = object : ProductAdapter.OnAddToCartClickListener {
            override fun onAddToCartClick(product: Product) {
                addToCart(product) // Call the addToCart function
            }
        }




        // Populate the category spinner with sample categories (you can replace this with actual categories from API)
        val categories = listOf("All", "Computers","Computer Components","Peripherals & Accessories","Storage & Networking")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Fetch products from the API
        ApiClient.fetchProducts { fetchedProducts ->
            runOnUiThread {
                products.clear()
                products.addAll(fetchedProducts)
                filteredProducts.addAll(fetchedProducts) // Initially show all products
                productAdapter.notifyDataSetChanged()
            }
        }
        productAdapter.productClickListener = object : ProductAdapter.OnProductClickListener {
            override fun onProductClick(product: Product) {
                // Open Product Details page when a product is clicked
                val intent = Intent(this@ProductListActivity, ProductDetailsActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
        }
        // Set up search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString(), categorySpinner.selectedItem.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up category filter functionality
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterProducts(searchEditText.text.toString(), categories[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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

    private fun filterProducts(query: String, category: String) {
        filteredProducts.clear()

        for (product in products) {
            val matchesSearch = product.name.lowercase().contains(query.lowercase())
            val matchesCategory = (category == "All" || product.category == category)

            if (matchesSearch && matchesCategory) {
                filteredProducts.add(product)
            }
        }

        productAdapter.notifyDataSetChanged() // Notify adapter about data changes
    }

    private fun addToCart(product: Product) {
        val userId = 13 // Replace with actual user ID if available

        // Check if the user exists in the cart database
        ApiClient.checkUserCart(userId) { cartExists ->
            if (cartExists) {
                // User has an existing cart, update the cart with the new product
                val existingCartItem = CartActivity.globalCartItems.find { it.id == product.id }

                if (existingCartItem != null) {
                    // Product is already in the cart, increment its quantity
                    existingCartItem.quantity++
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
                }

                // Prepare the cart data for updating
                val cartData = mapOf(
                    "userId" to userId,
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

                // Send update request to the server
                ApiClient.addToCart(cartData) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } else {
                // User does not have an existing cart, create a new cart
                val newCartItem = CartItem(
                    id = product.id,
                    name = product.name,
                    imageURL = product.imageUrl,
                    price = product.price,
                    quantity = 1
                )

                val newCartData = mapOf(
                    "userId" to userId,
                    "items" to listOf(newCartItem),
                    "totalPrice" to newCartItem.price
                )

                // Create a new cart
                ApiClient.createCart(newCartData) { success ->
                    runOnUiThread {
                        if (success) {
                            CartActivity.globalCartItems.add(newCartItem)
                            Toast.makeText(this, "${product.name} added to new cart", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to create cart", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
