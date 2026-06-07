package com.example.foodshare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.data.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository, private val sessionManager: SessionManager) : ViewModel() {

    var uiState by mutableStateOf<UserState>(UserState.Idle)
        private set

    fun loadCurrentUser() {
        viewModelScope.launch {
            uiState = UserState.Loading
            val res = repository.fetchCurrentUser()
            uiState = if (res.isSuccess) {
                val user = res.getOrNull()!!
                // Save user JSON locally for quick access
                sessionManager.saveUserJson(Gson().toJson(user))
                UserState.Success(user)
            } else {
                UserState.Error(res.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun saveUser(user: UserDto) {
        try {
            sessionManager.saveUserJson(Gson().toJson(user))
            uiState = UserState.Success(user)
        } catch (e: Exception) {
            uiState = UserState.Error(e.message ?: "Erreur lors de la sauvegarde du profil")
        }
    }

    fun getCachedUser(): UserState {
        val json = sessionManager.getUserJson() ?: return UserState.Idle
        return try {
            val user = Gson().fromJson(json, UserDto::class.java)
            UserState.Success(user)
        } catch (e: Exception) {
            UserState.Error(e.message ?: "Parse error")
        }
    }

    fun logout() {
        sessionManager.clearSession()
        uiState = UserState.Idle
    }
}
