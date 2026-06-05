package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.OffreDto

sealed class OffreDetailState {
    object Idle : OffreDetailState()
    object Loading : OffreDetailState()
    data class Success(val offer: OffreDto) : OffreDetailState()
    data class Error(val message: String) : OffreDetailState()
}
