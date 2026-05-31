package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.remote.dto.AuthResponse
import com.example.foodshare.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("api/auth/inscription")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/connexion")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/verification-email")
    suspend fun checkEmail(@Query("email") email: String): Response<Boolean>
}