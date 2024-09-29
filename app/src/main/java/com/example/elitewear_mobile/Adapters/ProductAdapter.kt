package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.elitewear_mobile.models.Product
import com.example.elitewear_mobile.R

class ProductAdapter(private val context: Context, private val products: List<Product>) : BaseAdapter() {
    override fun getCount(): Int = products.size
    override fun getItem(position: Int): Any = products[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)

        val productName = view.findViewById<TextView>(R.id.productName)
        val productPrice = view.findViewById<TextView>(R.id.productPrice)
        val productImage = view.findViewById<ImageView>(R.id.productImage)

        val product = products[position]
        productName.text = product.name
        productPrice.text = "$${product.price}"

        Glide.with(context).load(product.imageUrl).into(productImage)

        return view
    }
}