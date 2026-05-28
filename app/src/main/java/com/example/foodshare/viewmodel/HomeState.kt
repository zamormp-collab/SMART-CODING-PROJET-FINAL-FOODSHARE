package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.OffreDto

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(
        val offers: List<OffreDto>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

