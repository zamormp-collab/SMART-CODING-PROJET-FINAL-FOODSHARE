package com.example.foodshare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String,
    @SerializedName("userId")
    val userId: String,
    val role: String,
    val message: String? = null
)
