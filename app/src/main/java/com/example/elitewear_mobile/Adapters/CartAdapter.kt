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
            quantityTextView.text = cartItem.quantity.toString()
            itemPrice.text = "$${cartItem.price * cartItem.quantity}"

            updateTotalPrice() // Update total price
            notifyDataSetChanged()
        }

        // Decrement Button Logic
        decrementButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                quantityTextView.text = cartItem.quantity.toString()
                itemPrice.text = "$${cartItem.price * cartItem.quantity}"

                updateTotalPrice() // Update total price
                notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Cannot decrease further", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}