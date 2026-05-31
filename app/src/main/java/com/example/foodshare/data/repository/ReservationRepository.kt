package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.dto.ReservationDto

class ReservationRepository {
	// Retourne des réservations factices pour preview et développement local
	suspend fun fetchUserReservations(userId: String?): Result<List<ReservationDto>> {
		return try {
			val list = listOf(
				ReservationDto("r1", "1", "Double Beef", "Aujourd'hui - 12:30", "Confirmée"),
				ReservationDto("r2", "4", "Chicken Crisp", "Demain - 18:00", "En attente"),
				ReservationDto("r3", "3", "Fish Fillet", "27 Mai - 13:00", "Récupérée")
			)
			Result.success(list)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}