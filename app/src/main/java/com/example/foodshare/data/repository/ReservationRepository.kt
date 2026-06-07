package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.api.ReservationApiService
import com.example.foodshare.data.remote.dto.ReservationDto

class ReservationRepository(private val apiService: ReservationApiService) {
    suspend fun fetchUserReservations(userId: String?): Result<List<ReservationDto>> {
        return try {
            val response = apiService.getMyReservations()
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erreur lors de la récupération des réservations (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReservation(offreId: String): Result<ReservationDto> {
        return try {
            val response = apiService.createReservation(offreId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    403 -> "Seul un étudiant peut réserver une offre"
                    409 -> "Stock épuisé ou déjà réservé"
                    404 -> "Offre introuvable"
                    else -> "Échec de la réservation (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
