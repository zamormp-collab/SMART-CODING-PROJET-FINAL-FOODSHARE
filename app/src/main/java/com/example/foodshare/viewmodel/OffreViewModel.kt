package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository
import kotlinx.coroutines.launch

class OffreViewModel(
    private val repository: OffreRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    var uiState by mutableStateOf<OffreDetailState>(OffreDetailState.Idle)
        private set

    var reservationState by mutableStateOf<ReservationActionState>(ReservationActionState.Idle)
        private set

    fun loadOffer(offreId: String?) {
        viewModelScope.launch {
            if (offreId.isNullOrBlank()) {
                uiState = OffreDetailState.Error("Identifiant d'offre invalide")
                return@launch
            }

            uiState = OffreDetailState.Loading

            val result = repository.fetchOffreById(offreId)

            uiState = result.fold(
                onSuccess = { OffreDetailState.Success(it) },
                onFailure = { OffreDetailState.Error(it.message ?: "Impossible de charger l'offre") }
            )
        }
    }

    fun reserveOffer(offerId: String?) {
        if (offerId == null) return
        
        viewModelScope.launch {
            reservationState = ReservationActionState.Loading

            val result = reservationRepository.createReservation(offerId)

            reservationState = result.fold(
                onSuccess = { ReservationActionState.Success },
                onFailure = { ReservationActionState.Error(it.message ?: "Échec de la réservation") }
            )
        }
    }

    fun resetReservationState() {
        reservationState = ReservationActionState.Idle
    }
}

sealed class ReservationActionState {
    object Idle : ReservationActionState()
    object Loading : ReservationActionState()
    object Success : ReservationActionState()
    data class Error(val message: String) : ReservationActionState()
}
