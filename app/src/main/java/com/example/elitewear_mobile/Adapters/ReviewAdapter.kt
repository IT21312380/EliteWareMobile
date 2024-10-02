package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.elitewear_mobile.R
import com.example.elitewear_mobile.models.Review

class ReviewAdapter(private val context: Context, private val reviews:List<Review>):BaseAdapter() {
    override fun getCount(): Int = reviews.size
    override fun getItem(position: Int): Any = reviews[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.review_item, parent, false)

        val reviewName = view.findViewById<TextView>(R.id.reviewName)
        val reviewRate = view.findViewById<TextView>(R.id.reviewRate)
        val reviewDescription = view.findViewById<TextView>(R.id.reviewDescription)

        val review = reviews[position]
        reviewName.text = review.name
        reviewRate.text = "Rating: ${review.rate}"
        reviewDescription.text = review.description

        return view
    }


}