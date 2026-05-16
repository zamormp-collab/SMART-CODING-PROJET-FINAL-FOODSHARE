package com.example.foodshare.data.remote.dto

import androidx.compose.ui.semantics.Role

data class LoginResponse (
    val token: String,
    val userId: String,
    val role: String
)
