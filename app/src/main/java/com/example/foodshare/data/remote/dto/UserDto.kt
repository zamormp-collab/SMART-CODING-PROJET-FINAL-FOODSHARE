package com.example.foodshare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: String,
    val nom: String,
    val prenom: String?,
    val email: String,
    val role: String,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)

