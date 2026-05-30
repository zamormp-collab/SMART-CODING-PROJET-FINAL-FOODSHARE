package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.OffreDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OffreApiService {
	@GET("api/offres")
	suspend fun getOffres(): Response<List<OffreDto>>

	@GET("api/offres/{id}")
	suspend fun getOffre(@Path("id") id: String): Response<OffreDto>
}