package com.example.foodshare.data.repository

import com.example.foodshare.data.remote.api.OffreApiService
import com.example.foodshare.data.remote.dto.OffreDto

class OffreRepository(private val apiService: OffreApiService) {
    suspend fun fetchOffers(): Result<List<OffreDto>> {
        return try {
            val response = apiService.getOffres()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                // Si l'API échoue, on pourrait retourner des données factices ou l'erreur
                Result.failure(Exception("Erreur API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchOffreById(id: String): Result<OffreDto> {
        return try {
            val response = apiService.getOffreById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Offre non trouvée"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
