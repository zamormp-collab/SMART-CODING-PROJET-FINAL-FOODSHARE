package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.data.repository.ReservationRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

sealed class ReservationState {
	object Idle : ReservationState()
	object Loading : ReservationState()
	data class Success(val reservations: List<ReservationDto>) : ReservationState()
	data class DetailSuccess(val reservation: ReservationDto) : ReservationState()
	data class Created(val reservation: ReservationDto) : ReservationState()
	data class Error(val message: String) : ReservationState()
}

class ReservationViewModel(
	private val repository: ReservationRepository,
	private val sessionManager: SessionManager
) : ViewModel() {

	var uiState by mutableStateOf<ReservationState>(ReservationState.Idle)
		private set

	fun loadReservations() {
		viewModelScope.launch {
			uiState = ReservationState.Loading
			val userId = runCatching {
				sessionManager.getUserJson()
					?.let { Gson().fromJson(it, UserDto::class.java) }
					?.id
			}.getOrNull()

			val result = repository.fetchUserReservations(userId)
			uiState = if (result.isSuccess) {
				ReservationState.Success(result.getOrDefault(emptyList()))
			} else {
				ReservationState.Error(result.exceptionOrNull()?.message ?: "Impossible de charger les réservations")
			}
		}
	}

	fun createReservation(offer: OffreDto) {
		viewModelScope.launch {
			uiState = ReservationState.Loading
			val result = repository.createReservation(offer)
			uiState = if (result.isSuccess) {
				ReservationState.Created(result.getOrThrow())
			} else {
				ReservationState.Error(result.exceptionOrNull()?.message ?: "Impossible de créer la réservation")
			}
		}
	}

	fun loadReservationDetail(id: String) {
		viewModelScope.launch {
			uiState = ReservationState.Loading
			val result = repository.fetchReservationById(id)
			uiState = if (result.isSuccess) {
				ReservationState.DetailSuccess(result.getOrThrow())
			} else {
				ReservationState.Error(result.exceptionOrNull()?.message ?: "Impossible de charger le détail")
			}
		}
	}
}