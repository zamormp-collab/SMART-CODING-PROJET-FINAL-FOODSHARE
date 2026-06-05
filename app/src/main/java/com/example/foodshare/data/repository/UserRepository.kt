package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.data.remote.api.UserApiService
import com.example.foodshare.data.remote.dto.UserDto
import com.google.gson.Gson

class UserRepository(private val sessionManager: SessionManager) {
    suspend fun fetchCurrentUser(): Result<UserDto> {
        return try {
            // Try network first if token exists
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                try {
                    val userApi = RetrofitClient.createServiceWithAuth(sessionManager, UserApiService::class.java)
                    val resp = userApi.getProfile()
                    if (resp.isSuccessful) {
                        val user = resp.body()!!
                        // cache
                        sessionManager.saveUserJson(Gson().toJson(user))
                        return Result.success(user)
                    }
                } catch (e: Exception) {
                    // network failed, fallback to cache
                }
            }

            // Fallback to cached user
            val json = sessionManager.getUserJson()
            if (!json.isNullOrEmpty()) {
                val user = Gson().fromJson(json, UserDto::class.java)
                Result.success(user)
            } else {
                Result.failure(Exception("No cached user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


