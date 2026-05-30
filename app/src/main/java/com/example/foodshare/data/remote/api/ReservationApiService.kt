package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.ReservationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReservationApiService {

	@GET("api/reservations")
	suspend fun getReservations(): Response<List<ReservationDto>>

	@GET("api/reservations/{id}")
	suspend fun getReservation(@Path("id") id: String): Response<ReservationDto>
}