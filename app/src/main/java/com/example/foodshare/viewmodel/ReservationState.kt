package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.ReservationDto

sealed class ReservationState {
    object Idle : ReservationState()
    object Loading : ReservationState()
    data class Success(val reservations: List<ReservationDto>) : ReservationState()
    data class Error(val message: String) : ReservationState()
}
