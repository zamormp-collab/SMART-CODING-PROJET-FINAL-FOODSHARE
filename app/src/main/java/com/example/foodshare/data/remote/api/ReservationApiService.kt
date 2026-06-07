package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.ReservationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApiService {
    @GET("api/reservations")
    suspend fun getReservations(): Response<List<ReservationDto>>

    @GET("api/reservations/mes-reservations")
    suspend fun getMyReservations(): Response<List<ReservationDto>>

    @GET("api/reservations/user/{userId}")
    suspend fun getReservationsByUser(@Path("userId") userId: String): Response<List<ReservationDto>>

    @POST("api/reservations/offres/{offreId}")
    suspend fun createReservation(@Path("offreId") offreId: String): Response<ReservationDto>
}
