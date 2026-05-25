package com.example.foodshare.data.remote.api

import com.example.foodshare.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {

	// Assumes backend exposes current user at this endpoint
	@GET("api/auth/me")
	suspend fun getProfile(): Response<UserDto>
}


