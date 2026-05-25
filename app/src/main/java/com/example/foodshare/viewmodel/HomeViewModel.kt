package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val offreRepository: OffreRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    var uiState by mutableStateOf<HomeState>(HomeState.Idle)
        private set

    fun loadHomeData(userId: String? = null) {
        viewModelScope.launch {
            uiState = HomeState.Loading

            val offersResult = offreRepository.fetchOffers()
            val reservationsResult = reservationRepository.fetchUserReservations(userId)

            uiState = when {
                offersResult.isSuccess && reservationsResult.isSuccess -> {
                    HomeState.Success(
                        offers = offersResult.getOrDefault(emptyList()),
                        reservations = reservationsResult.getOrDefault(emptyList())
                    )
                }
                else -> {
                    HomeState.Error(
                        offersResult.exceptionOrNull()?.message
                            ?: reservationsResult.exceptionOrNull()?.message
                            ?: "Erreur lors du chargement de l'accueil"
                    )
                }
            }
        }
    }
}

