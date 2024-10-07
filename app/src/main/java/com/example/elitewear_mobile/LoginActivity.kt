package com.example.elitewear_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.elitewear_mobile.databinding.ActivityLoginBinding
import com.example.elitewear_mobile.models.User
import com.example.elitewear_mobile.network.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val user = User(0, "",email ,password, "","") // Username isn't needed for login
            loginUser(user)
        }
    }

    private fun loginUser(user: User) {
        val call = UserApiClient.authService.loginUser(user)
        call.enqueue(object : Callback<UserApiClient.AuthService.LoginResponse> {
            override fun onResponse(call: Call<UserApiClient.AuthService.LoginResponse>, response: Response<UserApiClient.AuthService.LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let { response ->
                        saveUserToPreferences(response) // Save user ID and username

                        // Optionally, you can fetch user profile using userId if needed

                        // Check user state (modify according to your user model)
                        when (response.username) {  // Use the username as a placeholder for state
                            "Pending" -> {
                                startActivity(Intent(this@LoginActivity, PendingActivity::class.java))
                            }
                            "Deactivated" -> {
                                startActivity(Intent(this@LoginActivity, DeactivateActivity::class.java))
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserApiClient.AuthService.LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserToPreferences(response: UserApiClient.AuthService.LoginResponse) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userEmail", binding.emailEditText.text.toString())
        editor.putString("username", response.username)
        editor.putInt("userId", response.userId) // Save userId as well
        editor.apply()
    }
}
