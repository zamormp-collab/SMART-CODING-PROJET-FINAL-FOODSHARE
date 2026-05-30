package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import kotlinx.coroutines.launch
import android.util.Patterns
import okhttp3.ResponseBody

class AuthViewModel(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = LoginState.Loading

            // Client-side validation
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                uiState = LoginState.EmailInvalid
                return@launch
            }

            if (password.isBlank()) {
                uiState = LoginState.PasswordInvalid
                return@launch
            }

            try {
                val request = LoginRequest(email = email, password = password)
                val response = authApiService.login(request)

                if (response.isSuccessful) {
                    response.body()?.token?.let(sessionManager::saveToken)

                    // After saving token, try to fetch the current user profile and cache it for immediate use
                    try {
                        val userApi = com.example.foodshare.data.remote.RetrofitClient.createServiceWithAuth(sessionManager, com.example.foodshare.data.remote.api.UserApiService::class.java)
                        val profileResp = userApi.getProfile()
                        if (profileResp.isSuccessful) {
                            val user = profileResp.body()
                            if (user != null) {
                                sessionManager.saveUserJson(com.google.gson.Gson().toJson(user))
                            }
                        }
                    } catch (_: Exception) {
                        // ignore profile fetch errors
                    }

                    uiState = LoginState.Success
                } else {
                    // Try to deduce whether it's an email or password problem
                    val code = response.code()
                    val errorBody = try {
                        response.errorBody()?.string()?.lowercase()
                    } catch (e: Exception) {
                        null
                    }

                    when {
                        code == 404 -> uiState = LoginState.EmailInvalid
                        code == 401 -> uiState = LoginState.PasswordInvalid
                        errorBody != null && ("email" in errorBody || "adresse" in errorBody) -> uiState = LoginState.EmailInvalid
                        errorBody != null && ("password" in errorBody || "mot de passe" in errorBody || "motdepasse" in errorBody) -> uiState = LoginState.PasswordInvalid
                        else -> uiState = LoginState.Error(response.message() ?: "Login failed")
                    }
                }
            } catch (e: Exception) {
                uiState = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }
}