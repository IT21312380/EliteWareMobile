package com.example.elitewear_mobile.network

import com.example.elitewear_mobile.models.UpdateUser
import com.example.elitewear_mobile.models.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


object UserApiClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5133") // Your .NET API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)


    interface AuthService {
        @POST("/api/user/register/")
        fun registerUser(@Body user: User): Call<Void>

        @POST("/api/user/login/")
        fun loginUser(@Body user: User): Call<LoginResponse> // Change the return type

        // Define a new data class for the login response
        data class LoginResponse(
            val userId: Int,
            val username: String
        )

        @GET("/api/user/{email}")
        fun fetchUserProfileByEmail(@Path("email") email: String): Call<User>

        @PUT("/api/user/deactivate/{email}")
        fun deactivateUser(@Path("email") email: String): Call<Void>

        @PUT("/api/user/requested/{email}")
        fun reactivateUser(@Path("email") email: String): Call<Void>

        @GET("/api/user/{email}")
        fun fetchUserProfileByEmailforUpdate(@Path("email") email: String): Call<UpdateUser>

        @PUT("/api/user/update/{email}")
        fun updateUser(@Path("email") email: String, @Body updatedUser: UpdateUser): Call<Void>






    }
}

