package com.example.foodshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.repository.UserRepository

class UserViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = UserRepository(sessionManager)
        return when (modelClass) {
            UserViewModel::class.java -> UserViewModel(repo, sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

