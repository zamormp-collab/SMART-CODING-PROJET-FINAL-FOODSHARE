package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository

class HomeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(OffreRepository(), ReservationRepository()) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

