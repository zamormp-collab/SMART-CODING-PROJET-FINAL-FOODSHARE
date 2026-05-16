package com.example.foodshare.data.remote.dto


data class LoginResponse (
    val token: String,
    val userId: String,
    val role: String
)
