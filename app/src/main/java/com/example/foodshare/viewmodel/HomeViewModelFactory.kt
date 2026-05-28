package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository

class HomeViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(OffreRepository(sessionManager), ReservationRepository()) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

