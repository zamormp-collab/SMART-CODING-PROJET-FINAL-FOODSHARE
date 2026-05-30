package com.example.foodshare.data.remote.dto

data class ReservationDto(
    val id: String?,
    val offreId: String?,
    val offreTitre: String?,
    val dateReservation: String?,
    val statut: String?,
    val imageUrl: String? = null
)

