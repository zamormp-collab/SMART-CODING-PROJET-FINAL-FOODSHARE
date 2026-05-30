package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.RegisterRequest
import kotlinx.coroutines.launch

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

                uiState = if (response.isSuccessful) {
                    // Save token if returned
                    response.body()?.token?.let(sessionManager::saveToken)

                    // Create and cache a UserDto locally from the registration form so Home can display the name immediately
                    try {
                        val user = com.example.foodshare.data.remote.dto.UserDto(
                            id = response.body()?.userId ?: "",
                            nom = nom,
                            prenom = prenom,
                            email = email,
                            role = response.body()?.role ?: "Utilisateur",
                            avatarUrl = null
                        )
                        sessionManager.saveUserJson(com.google.gson.Gson().toJson(user))
                    } catch (_: Exception) {
                        // ignore caching error
                    }

                    RegisterState.Success
                } else {
                    RegisterState.Error(response.message() ?: "Registration failed")
                }
            } catch (e: Exception) {
                uiState = RegisterState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

