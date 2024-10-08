package com.example.elitewear_mobile


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elitewear_mobile.models.UpdateUser
import com.example.elitewear_mobile.models.User
import com.example.elitewear_mobile.network.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserUpdateActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userid: String
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private var userId: Int = 0
    private lateinit var currentUser: UpdateUser
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_update)

        // Set up edge-to-edge insets for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch user email from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "") ?: ""
        userId = (sharedPreferences.getInt("userid",0) ?: 0)

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        saveButton = findViewById(R.id.saveButton)

        // Get user ID from intent
        val email = intent.getStringExtra("USER_EMAIL") ?: return

        // Fetch user profile
        fetchUserProfile(email)

       //  Set click listener for the save button
        saveButton.setOnClickListener {
            updateUserProfile(email)
        }

        val HomeButton = findViewById<ImageView>(R.id.navHomeUnClick)
        val ProfilePageButton = findViewById<ImageView>(R.id.navProfileUnClick)
        val CartPageButton = findViewById<ImageView>(R.id.navCartUnClick)
        val NotifyPageButton = findViewById<ImageView>(R.id.navNotifyUnClick)
        val OrderHistoryButton = findViewById<ImageView>(R.id.navOrderHistoryUnClick)

        OrderHistoryButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        HomeButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }
        ProfilePageButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        CartPageButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        NotifyPageButton.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUserProfile(email: String) {
        UserApiClient.authService.fetchUserProfileByEmailforUpdate(email).enqueue(object : Callback<UpdateUser> {
            override fun onResponse(call: Call<UpdateUser>, response: Response<UpdateUser>) {
                if (response.isSuccessful) {
                    currentUser = response.body()!!
                    usernameEditText.setText(currentUser.username)
                    emailEditText.setText(currentUser.email)
                    Log.d("ProfileActivity", "User Object: $currentUser")
                } else {
                    Toast.makeText(this@UserUpdateActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateUser>, t: Throwable) {
                Toast.makeText(this@UserUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


//    private fun updateUserProfile() {
//        val updatedUser = User(
//            id =currentUser.id,
//            username = usernameEditText.text.toString(),
//            email = emailEditText.text.toString(),
//            password = currentUser.password, // Keep existing password hash
//            state = currentUser.state ?: "",
//            requested = currentUser.requested ?: ""
//        )
//
//        UserApiClient.authService.updateUser(userid, updatedUser).enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@UserUpdateActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//                    finish() // Go back to the previous activity
//                } else {
//                    Toast.makeText(this@UserUpdateActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Toast.makeText(this@UserUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun updateUserProfile(oldEmail: String) {
        val updatedUser = UpdateUser(
            id = currentUser.id ?: "",
            username = usernameEditText.text.toString(),
            email = emailEditText.text.toString(), // Updated email
            passwordHash = currentUser.passwordHash ?: "", // Use the existing password hash or set a default
            state = currentUser.state ?: "",
            requested = currentUser.requested ?: ""
        )




        UserApiClient.authService.updateUser(oldEmail, updatedUser).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UserUpdateActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UserUpdateActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@UserUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
