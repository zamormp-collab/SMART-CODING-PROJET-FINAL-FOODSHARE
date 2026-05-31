package com.example.foodshare.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.api.UserApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class AuthViewModel(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val TAG = "AuthViewModel"

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    init {
        val existing = sessionManager.getToken()
        if (!existing.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val userApi = RetrofitClient.createServiceWithAuth(sessionManager, UserApiService::class.java)
                    val resp = userApi.getProfile()
                    if (resp.isSuccessful) {
                        resp.body()?.let {
                            sessionManager.saveUserJson(Gson().toJson(it))
                        }
                        uiState = LoginState.Success
                    } else {
                        sessionManager.clearSession()
                        uiState = LoginState.Idle
                    }
                } catch (e: Exception) {
                    uiState = LoginState.Idle
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "login() called with email=$email")
            uiState = LoginState.Loading

            try {
                val request = LoginRequest(email = email, password = password)
                val response = authApiService.login(request)

                Log.d(TAG, "API response code: ${response.code()}")

                uiState = when {
                    response.isSuccessful -> {
                        response.body()?.let {
                            sessionManager.saveToken(it.token)
                        }
                        Log.d(TAG, "Login successful")
                        LoginState.Success
                    }
                    response.code() == 401 -> LoginState.Error("Invalid email or password")
                    response.code() == 404 -> LoginState.Error("User not found")
                    else -> LoginState.Error(response.message() ?: "Login failed (HTTP ${response.code()})")
                }
            } catch (e: SocketTimeoutException) {
                val msg = "Connection timeout: API server is not responding.\nPlease check that the backend is running."
                Log.e(TAG, msg, e)
                uiState = LoginState.Error(msg)
            } catch (e: ConnectException) {
                val msg = "Connection error: Cannot reach API server.\nCheck internet connection and API URL."
                Log.e(TAG, msg, e)
                uiState = LoginState.Error(msg)
            } catch (e: Exception) {
                val msg = "Error: ${e.message ?: e.javaClass.simpleName}"
                Log.e(TAG, "Exception during login", e)
                uiState = LoginState.Error(msg)
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        uiState = LoginState.Idle
    }
}