package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.UserDto

sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Success(val user: UserDto) : UserState()
    data class Error(val message: String) : UserState()
}

