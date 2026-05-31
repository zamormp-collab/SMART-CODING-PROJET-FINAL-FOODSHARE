package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.dto.OffreDto

class OffreRepository {
	// Pour l'instant retourne des données factices. Plus tard, on appellera l'API.
	suspend fun fetchOffers(): Result<List<OffreDto>> {
		return try {
			val list = listOf(
				OffreDto("1", "Double Beef", "Burger maison avec viande double", 1, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("2", "Single Beef", "Simple et savoureux", 2, "2026-05-30", "Bordeaux", null, "1"),
				OffreDto("3", "Fish Fillet", "Poisson croustillant", 1, "2026-05-29", "Bordeaux", null, "1"),
				OffreDto("4", "Chicken Crisp", "Poulet croustillant", 1, "2026-05-31", "Bordeaux", null, "1")
			)
			Result.success(list)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}