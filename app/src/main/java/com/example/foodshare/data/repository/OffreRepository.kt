package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.dto.OffreDto

class OffreRepository {
	// Pour l'instant retourne des données factices. Plus tard, on appellera l'API.
	suspend fun fetchOffers(): Result<List<OffreDto>> {
		return try {
			val list = listOf(
				OffreDto("1", "Double Beef", "Burger maison avec viande double", "12", null, 1, null, null),
				OffreDto("2", "Single Beef", "Simple et savoureux", "9", null, 1, null, null),
				OffreDto("3", "Fish Fillet", "Poisson croustillant", "12", null, 1, null, null),
				OffreDto("4", "Chicken Crisp", "Poulet croustillant", "12", null, 1, null, null)
			)
			Result.success(list)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}