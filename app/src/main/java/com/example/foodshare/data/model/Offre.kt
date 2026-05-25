package com.example.foodshare.data.model

data class Offre(
    val id: String,
    val titre: String,
    val prix: String,
    val lieu: String,
    val disponibilite: String,
    val description: String,
    val donateur: String,
    val dateCreation: String,
    val imageUrl: String? = null
)

data class Reservation(
    val id: String,
    val offreId: String,
    val titre: String,
    val prix: String,
    val dateReservation: String,
    val statut: String, // "En attente", "Confirmée", "Récupérée", "Annulée"
    val lieu: String,
    val donateur: String,
    val dateExpiration: String? = null
)

