package com.example.elitewear_mobile

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.network.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeactivateActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deactivate)

        val reactivateButton = findViewById<Button>(R.id.ReactivateButton)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "") ?: ""

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        reactivateButton.setOnClickListener {
            reactivateUser(userEmail)
        }
    }



    private fun reactivateUser(email: String) {
        val call = UserApiClient.authService.reactivateUser(email)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DeactivateActivity, "Requested Activation", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(this@DeactivateActivity, "Failed to request", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DeactivateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}