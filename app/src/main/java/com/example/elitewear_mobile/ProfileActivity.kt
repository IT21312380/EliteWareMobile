package com.example.elitewear_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.models.User
import com.example.elitewear_mobile.network.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var username: String
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var deactivateButton: Button
    private lateinit var updateButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        emailTextView = findViewById(R.id.emailTextView)
        logoutButton = findViewById(R.id.logoutButton)
        deactivateButton = findViewById(R.id.deactivateButton)
        usernameTextView = findViewById(R.id.usernameTextView)
        updateButton = findViewById(R.id.updatebutton)

        // Fetch user email from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "") ?: ""
        username = sharedPreferences.getString("username","") ?:""

        if (userEmail.isNotEmpty()) {
            fetchUserProfile(userEmail)
        } else {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
        }
        logoutButton.setOnClickListener {
            logoutUser()
        }
        deactivateButton.setOnClickListener {
            deactivateUser(userEmail)
        }

        updateButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, UserUpdateActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail) // Pass the email to the next activity
            startActivity(intent)
        }



    }


    private fun fetchUserProfile(email: String) {
        val call = UserApiClient.authService.fetchUserProfileByEmail(email)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()

                    user?.let {
                        emailTextView.text = it.email
                        usernameTextView.text = it.username
                    }


                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun deactivateUser(email: String) {
        val call = UserApiClient.authService.deactivateUser(email)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, "Profile deactivated", Toast.LENGTH_SHORT).show()
                    logoutUser() // Log out after deactivation
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to deactivate profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun logoutUser() {
        // Clear all saved user data in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.clear() // This removes all stored data
        editor.apply()

        // Redirect to LoginActivity
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the activity stack
        startActivity(intent)
        finish()
    }




}
