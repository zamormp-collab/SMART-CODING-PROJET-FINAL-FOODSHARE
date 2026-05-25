package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.local.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = LoginState.Loading

            try {
                val request = LoginRequest(email = email, password = password)
                val response = authApiService.login(request)

                uiState = if (response.isSuccessful) {
                    // Save token if present in response body
                    response.body()?.let {
                        sessionManager.saveToken(it.token)
                        // Optionally save user id or JSON later
                    }
                    LoginState.Success
                } else {
                    LoginState.Error(response.message() ?: "Login failed")
                }
            } catch (e: Exception) {
                uiState = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }

    init {
        // If a token exists in secure storage, consider the user authenticated for auto-login.
        val existing = sessionManager.getToken()
        if (!existing.isNullOrEmpty()) {
            uiState = LoginState.Success
        }
    }

    fun logout() {
        sessionManager.clearSession()
        uiState = LoginState.Idle
    }
}