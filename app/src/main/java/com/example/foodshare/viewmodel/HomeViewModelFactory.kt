package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.OffreApiService
import com.example.foodshare.data.remote.api.ReservationApiService
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository

class HomeViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                // Utilisation du service avec authentification pour éviter le 403
                val offreApiService = RetrofitClient.createServiceWithAuth(sessionManager, OffreApiService::class.java)
                val reservationApiService = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)

                val offreRepository = OffreRepository(offreApiService)
                val reservationRepository = ReservationRepository(reservationApiService)
                HomeViewModel(offreRepository, reservationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
