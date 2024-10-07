package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.elitewear_mobile.EditReviewActivity
import com.example.elitewear_mobile.R
import com.example.elitewear_mobile.models.Review

class ReviewAdapter(private val context: Context, private val reviews: List<Review>) : BaseAdapter() {

    override fun getCount(): Int = reviews.size
    override fun getItem(position: Int): Any = reviews[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.review_item, parent, false)

        val reviewName = view.findViewById<TextView>(R.id.reviewName)
        val reviewRate = view.findViewById<TextView>(R.id.reviewRate)
        val reviewDescription = view.findViewById<TextView>(R.id.reviewDescription)
        val editReviewButton = view.findViewById<Button>(R.id.editReviewButton)

        val review = reviews[position]
        reviewName.text = review.name
        reviewRate.text = "Rating: ${review.rate}"
        reviewDescription.text = review.description

        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("username", null)


        if (review.name == loggedInUsername) {
            editReviewButton.visibility = View.VISIBLE
        } else {
            editReviewButton.visibility = View.GONE
        }

        editReviewButton.setOnClickListener {
            val intent = Intent(context, EditReviewActivity::class.java)
            intent.putExtra("reviewId", review.id)  // Assuming each review has an ID
            context.startActivity(intent)
        }

        return view
    }
}
