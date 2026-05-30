package com.example.foodshare.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.ConnectException

class AuthViewModel(
    private val authApiService: AuthApiService
) : ViewModel() {

    private val TAG = "AuthViewModel"

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

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
                        Log.d(TAG, "Login successful")
                        LoginState.Success
                    }
                    response.code() == 401 -> {
                        val msg = "Invalid email or password"
                        Log.w(TAG, msg)
                        LoginState.Error(msg)
                    }
                    response.code() == 404 -> {
                        val msg = "User not found"
                        Log.w(TAG, msg)
                        LoginState.Error(msg)
                    }
                    else -> {
                        val msg = response.message() ?: "Login failed (HTTP ${response.code()})"
                        Log.w(TAG, msg)
                        LoginState.Error(msg)
                    }
                }
            } catch (e: SocketTimeoutException) {
                val msg = "Connection timeout: API server is not responding.\nPlease check that the backend is running."
                Log.e(TAG, "SocketTimeoutException: $msg", e)
                uiState = LoginState.Error(msg)
            } catch (e: ConnectException) {
                val msg = "Connection error: Cannot reach API server.\nCheck internet connection and API URL."
                Log.e(TAG, "ConnectException: $msg", e)
                uiState = LoginState.Error(msg)
            } catch (e: Exception) {
                val msg = "Error: ${e.message ?: e.javaClass.simpleName}"
                Log.e(TAG, "Exception during login", e)
                uiState = LoginState.Error(msg)
            }
        }
    }
}