package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.api.ReservationApiService
import com.example.foodshare.data.remote.dto.ReservationDto

class ReservationRepository(private val apiService: ReservationApiService) {
    suspend fun fetchUserReservations(userId: String?): Result<List<ReservationDto>> {
        return try {
            val response = if (userId != null) {
                apiService.getReservationsByUser(userId)
            } else {
                apiService.getReservations()
            }
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erreur lors de la récupération des réservations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReservation(reservation: ReservationDto): Result<ReservationDto> {
        return try {
            val response = apiService.createReservation(reservation)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Échec de la réservation"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
