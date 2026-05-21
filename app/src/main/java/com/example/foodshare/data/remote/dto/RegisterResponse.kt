package com.example.foodshare.data.remote.dto

data class RegisterResponse(
    val token: String,
    val userId: String,
    val role: String,
    val message: String? = null
)

