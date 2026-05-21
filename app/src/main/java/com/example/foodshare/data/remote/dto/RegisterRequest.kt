package com.example.foodshare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val nom: String,
    val prenom: String,
    val email: String,
    @SerializedName("motDePasse")
    val motDePasse: String,
    val role: String,
    val adresse: String,
    val telephone: String
)

