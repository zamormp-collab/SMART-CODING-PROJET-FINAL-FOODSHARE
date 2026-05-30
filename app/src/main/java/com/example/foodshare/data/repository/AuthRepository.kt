package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.remote.dto.LoginResponse

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    sessionManager.saveToken(it.token)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}