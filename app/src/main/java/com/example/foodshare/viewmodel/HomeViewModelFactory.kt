package com.example.foodshare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.db.AppDatabase
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                val database = AppDatabase.getDatabase(context)
                val offreRepository = OffreRepository(
                    RetrofitClient.offreApiService,
                    database.offreDao()
                )
                val reservationRepository = ReservationRepository(RetrofitClient.reservationApiService)
                HomeViewModel(offreRepository, reservationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
