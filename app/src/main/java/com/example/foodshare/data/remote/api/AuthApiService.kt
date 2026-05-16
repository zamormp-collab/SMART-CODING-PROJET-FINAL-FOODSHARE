package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}