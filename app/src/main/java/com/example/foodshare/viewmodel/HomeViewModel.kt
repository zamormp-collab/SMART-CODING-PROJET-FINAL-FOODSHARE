package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val offreRepository: OffreRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    var uiState by mutableStateOf<HomeState>(HomeState.Idle)
        private set

    private var refreshJob: Job? = null

    fun loadHomeData(userId: String? = null, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                uiState = HomeState.Loading
            }

            val offersResult = offreRepository.fetchOffers()
            val reservationsResult = reservationRepository.fetchUserReservations(userId)

            if (offersResult.isSuccess) {
                uiState = HomeState.Success(
                    offers = offersResult.getOrDefault(emptyList()),
                    reservations = reservationsResult.getOrDefault(emptyList())
                )
            } else if (!silent) {
                uiState = HomeState.Error(
                    offersResult.exceptionOrNull()?.message ?: "Erreur lors du chargement des offres"
                )
            }
        }
    }

    fun startAutoRefresh(userId: String? = null) {
        if (refreshJob != null) return
        
        refreshJob = viewModelScope.launch {
            while (isActive) {
                loadHomeData(userId, silent = true)
                delay(1000)
            }
        }
    }

    fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}

