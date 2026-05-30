package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationRepository(private val sessionManager: SessionManager) {

	fun fetchUserReservations(userId: String?): Result<List<ReservationDto>> {
		return Result.success(cachedReservations())
	}

	fun createReservation(offer: OffreDto): Result<ReservationDto> {
		return try {
			val offerId = offer.id?.takeIf { it.isNotBlank() }
			if (offerId == null) {
				return Result.failure(IllegalArgumentException("Offre invalide"))
			}

			val current = cachedReservations().toMutableList()
			val created = ReservationDto(
				id = "res-${System.currentTimeMillis()}",
				offreId = offerId,
				offreTitre = offer.title?.takeIf { it.isNotBlank() } ?: "Offre",
				dateReservation = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date()),
				statut = "Confirmée"
			)
			current.add(0, created)
			sessionManager.saveReservationsJson(Gson().toJson(current))
			Result.success(created)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	fun fetchReservationById(id: String): Result<ReservationDto> {
		return try {
			val reservation = cachedReservations().firstOrNull { it.id == id }
			if (reservation != null) {
				Result.success(reservation)
			} else {
				Result.failure(Exception("Réservation introuvable"))
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	private fun cachedReservations(): List<ReservationDto> {
		val json = sessionManager.getReservationsJson().orEmpty()
		if (json.isBlank()) return emptyList()
		return try {
			Gson().fromJson(json, Array<ReservationDto>::class.java)?.toList().orEmpty()
		} catch (_: Exception) {
			emptyList()
		}
	}
}