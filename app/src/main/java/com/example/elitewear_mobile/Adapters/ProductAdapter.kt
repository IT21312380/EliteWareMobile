package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.elitewear_mobile.models.Product
import com.example.elitewear_mobile.R

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>
) : BaseAdapter() {

    override fun getCount(): Int = products.size
    override fun getItem(position: Int): Any = products[position]
    override fun getItemId(position: Int): Long = position.toLong()

    // Add an interface for handling button clicks
    interface OnAddToCartClickListener {
        fun onAddToCartClick(product: Product)
    }

    var addToCartClickListener: OnAddToCartClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)

        val productName = view.findViewById<TextView>(R.id.productName)
        val productPrice = view.findViewById<TextView>(R.id.productPrice)
        val productImage = view.findViewById<ImageView>(R.id.productImage)
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton) // Add a button in your layout

        val product = products[position]
        productName.text = product.name
        productPrice.text = "$${product.price}"

        Glide.with(context).load(product.imageUrl).into(productImage)

        // Handle the Add to Cart button click
        addToCartButton.setOnClickListener {
            addToCartClickListener?.onAddToCartClick(product)
        }

        return view
    }
}
