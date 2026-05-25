package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.UserApiService
import com.example.foodshare.data.remote.dto.UserDto

class UserRepository(private val sessionManager: SessionManager) {

    private val userApi = RetrofitClient.createServiceWithAuth(sessionManager, UserApiService::class.java)

    suspend fun fetchCurrentUser(): Result<UserDto> {
        return try {
            val resp = userApi.getProfile()
            if (resp.isSuccessful) {
                resp.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Failed: ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

