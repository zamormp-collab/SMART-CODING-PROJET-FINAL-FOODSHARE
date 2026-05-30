package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.ReservationApiService
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationRepository(private val sessionManager: SessionManager) {

	// Récupère la liste des réservations depuis l'API si possible, sinon utilise le cache
	suspend fun fetchUserReservations(userId: String?): Result<List<ReservationDto>> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)
			val response = api.getReservations()
			if (response.isSuccessful) {
				val list = response.body().orEmpty()
				// Met en cache la liste
				sessionManager.saveReservationsJson(Gson().toJson(list))
				Result.success(list)
			} else {
				// si l'API répond mais avec une erreur, retourner le cache si présent
				val cached = cachedReservations()
				if (cached.isNotEmpty()) Result.success(cached) else Result.failure(Exception("Erreur serveur: ${response.code()}"))
			}
		} catch (e: Exception) {
			// en cas d'exception réseau, retourner le cache si disponible
			val cached = cachedReservations()
			if (cached.isNotEmpty()) Result.success(cached) else Result.failure(e)
		}
	}

	suspend fun createReservation(offer: OffreDto): Result<ReservationDto> {
		return try {
			val offerId = offer.id?.takeIf { it.isNotBlank() }
			if (offerId == null) {
				return Result.failure(IllegalArgumentException("Offre invalide"))
			}

			val api = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)
			val response = api.reserveOffer(offerId)
			if (response.isSuccessful) {
				val createdFromApi = response.body()
				if (createdFromApi != null) {
					val current = cachedReservations().toMutableList()
					current.add(0, createdFromApi)
					sessionManager.saveReservationsJson(Gson().toJson(current))
					return Result.success(createdFromApi)
				}
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

	// Récupère un détail de réservation depuis l'API si possible, sinon depuis le cache
	suspend fun fetchReservationById(id: String): Result<ReservationDto> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)
			val response = api.getReservation(id)
			if (response.isSuccessful) {
				val res = response.body()
				if (res != null) {
					Result.success(res)
				} else {
					Result.failure(Exception("Réservation introuvable"))
				}
			} else {
				// fallback cache
				val reservation = cachedReservations().firstOrNull { it.id == id }
				if (reservation != null) Result.success(reservation) else Result.failure(Exception("Réservation introuvable (API ${response.code()})"))
			}
		} catch (e: Exception) {
			val reservation = cachedReservations().firstOrNull { it.id == id }
			if (reservation != null) Result.success(reservation) else Result.failure(e)
		}
	}

	// Annuler / supprimer une réservation via l'API et mettre à jour le cache local
	suspend fun cancelReservation(id: String): Result<Boolean> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)
			val response = api.cancelReservation(id)
			if (response.isSuccessful || response.code() == 204) {
				// retirer du cache local
				val updated = cachedReservations().filterNot { it.id == id }
				sessionManager.saveReservationsJson(Gson().toJson(updated))
				Result.success(true)
			} else if (response.code() == 404) {
				// déjà supprimée côté serveur, enlever du cache aussi
				val updated = cachedReservations().filterNot { it.id == id }
				sessionManager.saveReservationsJson(Gson().toJson(updated))
				Result.success(true)
			} else {
				Result.failure(Exception("Impossible d'annuler la réservation: ${response.code()}"))
			}
		} catch (e: Exception) {
			// en cas d'erreur réseau, tenter de retirer du cache pour garder l'UI cohérente
			val updated = cachedReservations().filterNot { it.id == id }
			sessionManager.saveReservationsJson(Gson().toJson(updated))
			Result.success(true)
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