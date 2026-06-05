package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService

class AuthViewModelFactory(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(authApiService, sessionManager) as T
            RegisterViewModel::class.java -> RegisterViewModel(authApiService, sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
