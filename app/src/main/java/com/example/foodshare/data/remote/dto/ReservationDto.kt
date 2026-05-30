package com.example.foodshare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReservationDto(
    val id: String?,
    val offreId: String?,
    val offreTitre: String?,
    val dateReservation: String?,
    val statut: String?,
    @SerializedName(value = "imageUrl", alternate = ["offreImageUrl", "offerImageUrl", "image"])
    val imageUrl: String? = null
)

