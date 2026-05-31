package com.example.foodshare.domain.uscase

import com.example.foodshare.data.repository.AuthRepository


class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.login(email, password)
}