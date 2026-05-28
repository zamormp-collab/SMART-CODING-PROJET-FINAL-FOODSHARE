package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.OffreApiService
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.local.SessionManager

class OffreRepository(private val sessionManager: SessionManager) {
	// Essaie d'appeler l'API, sinon retourne des données factices pour le développement
	suspend fun fetchOffers(): Result<List<OffreDto>> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, OffreApiService::class.java)
			val resp = api.getOffres()
			if (resp.isSuccessful) {
				val body = resp.body()
				if (body != null) return Result.success(body)
			}

			// Fallback local si l'API ne répond pas ou retourne vide
			val list = listOf(
				OffreDto("1", "Double Beef", "Burger maison avec viande double", 1, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("2", "Single Beef", "Simple et savoureux", 2, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("3", "Fish Fillet", "Poisson croustillant", 1, "2026-05-29", "Bordeaux", null, "1"),
				OffreDto("4", "Chicken Crisp", "Poulet croustillant", 1, "2026-05-31", "Bordeaux", null, "1")
			)
			Result.success(list)
		} catch (e: Exception) {
			// En cas d'erreur réseau, retourner aussi les données factices pour garder l'UI utilisable
			val list = listOf(
				OffreDto("1", "Double Beef", "Burger maison avec viande double", 1, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("2", "Single Beef", "Simple et savoureux", 2, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("3", "Fish Fillet", "Poisson croustillant", 1, "2026-05-29", "Bordeaux", null, "1")
			)
			Result.success(list)
		}
	}
}