package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.OffreApiService
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.local.SessionManager

class OffreRepository(private val sessionManager: SessionManager) {
	suspend fun fetchOffers(): Result<List<OffreDto>> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, OffreApiService::class.java)
			val resp = api.getOffres()
			if (resp.isSuccessful) {
				val body = resp.body()
				if (body != null) return Result.success(body)
			}
			Result.failure(Exception("Impossible de charger les offres depuis l'API (${resp.code()})"))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	suspend fun fetchOfferById(id: String): Result<OffreDto> {
		return try {
			val api = RetrofitClient.createServiceWithAuth(sessionManager, OffreApiService::class.java)
			val resp = api.getOffre(id)
			if (resp.isSuccessful) {
				val body = resp.body()
				if (body != null) return Result.success(body)
			}
			Result.failure(Exception("Impossible de charger le détail de l'offre (${resp.code()})"))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}