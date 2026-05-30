package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.repository.OffreRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val offreRepository: OffreRepository
) : ViewModel() {

    var uiState by mutableStateOf<HomeState>(HomeState.Idle)
        private set

    fun loadHomeData() {
        viewModelScope.launch {
            uiState = HomeState.Loading

            val offersResult = offreRepository.fetchOffers()

            uiState = when {
                offersResult.isSuccess -> {
                    HomeState.Success(offers = offersResult.getOrDefault(emptyList()))
                }
                else -> {
                    HomeState.Error(
                        offersResult.exceptionOrNull()?.message
                            ?: "Erreur lors du chargement de l'accueil"
                    )
                }
            }
        }
    }
}

