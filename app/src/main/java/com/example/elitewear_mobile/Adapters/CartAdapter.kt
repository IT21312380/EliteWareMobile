package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.R

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val updateTotalPrice: () -> Unit // A callback to update total price in activity
) : BaseAdapter() {

    override fun getCount(): Int = cartItems.size
    override fun getItem(position: Int): Any = cartItems[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)

        val itemImage = view.findViewById<ImageView>(R.id.itemImage)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        val itemPrice = view.findViewById<TextView>(R.id.itemPrice)
        val quantityTextView = view.findViewById<TextView>(R.id.quantityTextView)
        val incrementButton = view.findViewById<Button>(R.id.incrementButton)
        val decrementButton = view.findViewById<Button>(R.id.decrementButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val cartItem = cartItems[position]

        // Set initial values
        itemName.text = cartItem.name
        itemPrice.text = "$${cartItem.price * cartItem.quantity}"
        quantityTextView.text = cartItem.quantity.toString()

        // Load the image using Glide or any other image loading library
        Glide.with(context).load(cartItem.imageURL).into(itemImage)

        // Increment Button Logic
        incrementButton.setOnClickListener {
            cartItem.quantity++
            updateItemView(cartItem, itemPrice, quantityTextView)

            updateDatabase(cartItems) // Update the database
            updateTotalPrice() // Update total price
        }
        deleteButton.setOnClickListener {
            ApiClient.removeCartItem(12, cartItem.id) { success ->  // 12 is the cartId, cartItem.id is the itemId
                if (success) {
                    cartItems.removeAt(position) // Remove the item from the list
                    notifyDataSetChanged() // Refresh the list
                    updateTotalPrice() // Update the total price in the activity
                    Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Decrement Button Logic
        decrementButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                updateItemView(cartItem, itemPrice, quantityTextView)

                updateDatabase(cartItems) // Update the database
                updateTotalPrice() // Update total price
            } else {
                Toast.makeText(context, "Cannot decrease further", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    private fun updateItemView(cartItem: CartItem, itemPrice: TextView, quantityTextView: TextView) {
        quantityTextView.text = cartItem.quantity.toString()
        itemPrice.text = "$${cartItem.price * cartItem.quantity}"
        notifyDataSetChanged() // Notify the adapter to refresh the list
    }

    private fun updateDatabase(cartItems: List<CartItem>) {
        // Prepare cart data structure
        val cartData = mapOf(
            "id" to 12, // Unique cart ID (consider generating a new ID)
            "userId" to 0, // Replace with actual user ID if available
            "items" to cartItems.map { cartItem ->
                mapOf(
                    "id" to cartItem.id,
                    "name" to cartItem.name,
                    "imageURL" to cartItem.imageURL,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity
                )
            },
            "totalPrice" to cartItems.sumOf { it.price * it.quantity }
        )

        // Make API call to update the cart
        ApiClient.addToCart(cartData) { success ->
            if (success) {
                // Successfully updated the cart in the database
                println("Updated cart successfully.")
            } else {
                // Handle the error (e.g., show a message to the user)
                println("Failed to update cart.")
            }
        }
    }

}
