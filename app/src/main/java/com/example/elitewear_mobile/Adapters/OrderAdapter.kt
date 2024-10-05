// OrderAdapter.kt
package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.elitewear_mobile.models.Order
import com.example.elitewear_mobile.R

class OrderAdapter(context: Context, orders: List<Order>) : ArrayAdapter<Order>(context, 0, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val order = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)

        val orderIdTextView = view.findViewById<TextView>(R.id.orderIdTextView)
        val orderStatusTextView = view.findViewById<TextView>(R.id.orderStatusTextView)
        val orderTotalPriceTextView = view.findViewById<TextView>(R.id.orderTotalPriceTextView)
        val orderItemsTextView = view.findViewById<TextView>(R.id.orderItemsTextView)

        orderIdTextView.text = "Order ID: ${order?.id}"
        orderStatusTextView.text = "Status: ${order?.status}"
        orderTotalPriceTextView.text = "Total Price: ${order?.totalPrice}"

        // Display the order items
        val itemsDescription = order?.items?.joinToString { "${it.name} (Qty: ${it.qty})" }
        orderItemsTextView.text = "Items: $itemsDescription"

        return view
    }
}
