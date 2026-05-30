package com.example.foodshare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    @SerializedName("motDePasse")
    val password: String
)

