package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.RegisterRequest
import com.example.foodshare.data.local.SessionManager
import kotlinx.coroutines.launch
import android.util.Log

class RegisterViewModel(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf<RegisterState>(RegisterState.Idle)
        private set

    fun register(
        nom: String,
        prenom: String,
        email: String,
        motDePasse: String,
        role: String,
        adresse: String,
        telephone: String
    ) {
        viewModelScope.launch {
            Log.d(TAG, "register() called with email=$email, nom=$nom")
            uiState = RegisterState.Loading

            try {
                val request = RegisterRequest(
                    nom = nom,
                    prenom = prenom,
                    email = email,
                    motDePasse = motDePasse,
                    role = role,
                    adresse = adresse,
                    telephone = telephone
                )

                val response = authApiService.register(request)

<<<<<<< Updated upstream
                uiState = if (response.isSuccessful) {
                    // Save token if present in response body
                    response.body()?.let {
                        sessionManager.saveToken(it.token)
                    }
                    RegisterState.Success
=======
                if (response.isSuccessful) {
                    response.body()?.token?.let(sessionManager::saveToken)
                    uiState = RegisterState.Success
                    Log.d(TAG, "register success")
>>>>>>> Stashed changes
                } else {
                    val err = response.message() ?: "Registration failed"
                    uiState = RegisterState.Error(err)
                    Log.w(TAG, "register failed: code=${response.code()} message=$err body=${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "register exception", e)
                uiState = RegisterState.Error(e.message ?: "An error occurred")
            }
        }
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}

