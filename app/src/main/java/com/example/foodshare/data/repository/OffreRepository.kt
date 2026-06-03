package com.example.foodshare.data.repository

import com.example.foodshare.data.local.db.OffreDao
import com.example.foodshare.data.local.db.toDto
import com.example.foodshare.data.local.db.toEntity
import com.example.foodshare.data.remote.api.OffreApiService
import com.example.foodshare.data.remote.dto.OffreDto

class OffreRepository(
    private val apiService: OffreApiService,
    private val offreDao: OffreDao? = null
) {
    suspend fun fetchOffers(): Result<List<OffreDto>> {
        return try {
            val response = apiService.getOffres()
            if (response.isSuccessful) {
                val remoteOffers = response.body() ?: emptyList()
                
                // Sauvegarde en cache local si le DAO est disponible
                offreDao?.let { dao ->
                    val entities = remoteOffers.mapNotNull { it.toEntity() }
                    if (entities.isNotEmpty()) {
                        dao.clearAll()
                        dao.insertOffres(entities)
                    }
                }
                
                Result.success(remoteOffers)
            } else {
                // Si l'API échoue (ex: 404 ou 500), on tente de lire le cache
                loadFromCache()
            }
        } catch (e: Exception) {
            // Si pas d'internet ou ngrok coupé
            loadFromCache()
        }
    }

    private suspend fun loadFromCache(): Result<List<OffreDto>> {
        return if (offreDao != null) {
            val cached = offreDao.getAllOffres().map { it.toDto() }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(Exception("Aucune donnée en cache et serveur injoignable"))
            }
        } else {
            Result.failure(Exception("Erreur réseau et cache indisponible"))
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
