package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.elitewear_mobile.models.CartItem
import com.example.elitewear_mobile.R

class CartAdapter(private val context: Context, private val cartItems: List<CartItem>) : BaseAdapter() {
    override fun getCount(): Int = cartItems.size
    override fun getItem(position: Int): Any = cartItems[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)

        val itemName = view.findViewById<TextView>(R.id.itemName)
        val itemPrice = view.findViewById<TextView>(R.id.itemPrice)
        val itemQuantity = view.findViewById<TextView>(R.id.itemQuantity)

        val cartItem = cartItems[position]
        itemName.text = cartItem.name
        itemPrice.text = "Price: $${cartItem.price}"
        itemQuantity.text = "Quantity: ${cartItem.quantity}"

        return view
    }
}