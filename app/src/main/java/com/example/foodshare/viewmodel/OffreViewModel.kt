package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.repository.OffreRepository
import kotlinx.coroutines.launch

class OffreViewModel(private val repository: OffreRepository) : ViewModel() {

    var uiState by mutableStateOf<OffreDetailState>(OffreDetailState.Idle)
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
}