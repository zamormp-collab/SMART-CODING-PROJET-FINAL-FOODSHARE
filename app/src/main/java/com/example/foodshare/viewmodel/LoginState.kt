package com.example.foodshare.viewmodel

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()

    // Field-specific validation states
    object EmailInvalid : LoginState()
    object PasswordInvalid : LoginState()

    // Generic error with message
    data class Error(val message: String) : LoginState()
}