package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.ReservationApiService
import com.example.foodshare.data.repository.ReservationRepository

class ReservationViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ReservationViewModel::class.java -> {
                // Utilisation du service avec authentification pour éviter le 403
                val apiService = RetrofitClient.createServiceWithAuth(sessionManager, ReservationApiService::class.java)
                val repository = ReservationRepository(apiService)
                ReservationViewModel(repository, sessionManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
