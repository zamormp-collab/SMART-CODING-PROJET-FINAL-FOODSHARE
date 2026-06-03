package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReviewDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.data.repository.ReservationRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf<ReservationState>(ReservationState.Idle)
        private set

    var isSubmittingReview by mutableStateOf(false)
        private set

    fun loadReservations() {
        viewModelScope.launch {
            uiState = ReservationState.Loading

            val userIdResult = resolveUserId()
            if (userIdResult.isFailure) {
                uiState = ReservationState.Error(userIdResult.exceptionOrNull()?.message ?: "Profil utilisateur invalide")
                return@launch
            }

            val result = repository.fetchUserReservations(userIdResult.getOrNull())
            uiState = result.fold(
                onSuccess = { ReservationState.Success(it) },
                onFailure = { ReservationState.Error(it.message ?: "Erreur lors du chargement") }
            )
        }
    }

    fun submitReview(reservationId: String, note: Int, commentaire: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSubmittingReview = true
            val review = ReviewDto(reservationId, note, commentaire)
            val result = repository.submitReview(review)
            
            isSubmittingReview = false
            if (result.isSuccess) {
                onSuccess()
                loadReservations()
            }
        }
    }

    private fun resolveUserId(): Result<String?> {
        val json = sessionManager.getUserJson() ?: return Result.success(null)
        return try {
            val user = Gson().fromJson(json, UserDto::class.java)
            Result.success(user?.id)
        } catch (_: Exception) {
            Result.failure(Exception("Profil utilisateur invalide"))
        }
    }
}
