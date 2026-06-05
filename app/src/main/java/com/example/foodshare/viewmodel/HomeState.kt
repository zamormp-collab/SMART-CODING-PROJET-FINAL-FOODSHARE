package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(
        val offers: List<OffreDto>,
        val reservations: List<ReservationDto>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

