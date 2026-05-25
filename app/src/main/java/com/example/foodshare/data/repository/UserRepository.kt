package com.example.foodshare.data.repository

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.UserDto
import com.google.gson.Gson

class UserRepository(private val sessionManager: SessionManager) {
    suspend fun fetchCurrentUser(): Result<UserDto> {
        return try {
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

