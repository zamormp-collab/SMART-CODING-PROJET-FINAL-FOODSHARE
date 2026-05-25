package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.api.UserApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
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
        // If a token exists in secure storage, validate it with the server
        val existing = sessionManager.getToken()
        if (!existing.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val userApi = RetrofitClient.createServiceWithAuth(sessionManager, UserApiService::class.java)
                    val resp = userApi.getProfile()
                    if (resp.isSuccessful) {
                        resp.body()?.let {
                            // save cached user
                            sessionManager.saveUserJson(com.google.gson.Gson().toJson(it))
                        }
                        uiState = LoginState.Success
                    } else {
                        // token invalid or expired
                        sessionManager.clearSession()
                        uiState = LoginState.Idle
                    }
                } catch (e: Exception) {
                    // network error — keep Idle so user can login manually
                    uiState = LoginState.Idle
                }
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        uiState = LoginState.Idle
    }
}