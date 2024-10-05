package com.example.elitewear_mobile.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.elitewear_mobile.R
import com.example.elitewear_mobile.models.Notification

class NotificationAdapter (
    private val context: Context,
    private val notifications:List<Notification>

):BaseAdapter(){
    override fun getCount(): Int =notifications.size
    override fun getItem(position: Int): Any =notifications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int,  convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)

        val notificationMessage = view.findViewById<TextView>(R.id.notificationMessage)
        val createdTime = view.findViewById<TextView>(R.id.createdTime)

        val notification = notifications[position]
        notificationMessage.text = notification.Message
        createdTime.text = notification.createdAt.toString()
        return view
    }

}