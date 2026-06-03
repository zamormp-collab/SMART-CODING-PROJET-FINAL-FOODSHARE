package com.example.foodshare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.db.AppDatabase
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.repository.OffreRepository

class OffreViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            OffreViewModel::class.java -> {
                val database = AppDatabase.getDatabase(context)
                val repository = OffreRepository(
                    RetrofitClient.offreApiService,
                    database.offreDao()
                )
                OffreViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
