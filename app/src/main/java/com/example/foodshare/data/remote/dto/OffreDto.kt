package com.example.foodshare.data.remote.dto

data class OffreDto(
    val id: String?,
    val title: String?,
    val description: String?,
    val quantity: Int?,
    val expirationDate: String?,
    val location: String?,
    val imageUrl: String?,
    val userId: String?
)