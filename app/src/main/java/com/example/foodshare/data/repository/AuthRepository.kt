package com.example.foodshare.data.repository

import android.util.Log
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.LoginRequest
import com.example.foodshare.data.remote.dto.AuthResponse
import com.example.foodshare.data.remote.dto.RegisterRequest
import java.net.SocketTimeoutException
import java.net.ConnectException

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    private val TAG = "AuthRepository"

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Attempting login for $email")
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Login successful, saving token")
                    sessionManager.saveToken(it.token)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body from API"))
            } else {
                val errorMsg = "Login failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            val msg = "Connection timeout: API is not responding. Please check the backend server."
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        } catch (e: ConnectException) {
            val msg = "Connection error: Cannot reach the API server. Check internet and URL."
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = "Login error: ${e.message}"
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        }
    }

    suspend fun register(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        role: String,
        adresse: String,
        telephone: String
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Attempting registration for $email")
            val response = api.register(
                RegisterRequest(
                    nom = nom,
                    prenom = prenom,
                    email = email,
                    motDePasse = password,
                    role = role,
                    adresse = adresse,
                    telephone = telephone
                )
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Registration successful, saving token")
                    sessionManager.saveToken(it.token)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body from API"))
            } else {
                val errorMsg = "Registration failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            val msg = "Connection timeout: API is not responding. Please check the backend server."
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        } catch (e: ConnectException) {
            val msg = "Connection error: Cannot reach the API server. Check internet and URL."
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = "Registration error: ${e.message}"
            Log.e(TAG, msg, e)
            Result.failure(Exception(msg))
        }
    }
}