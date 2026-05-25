package com.example.foodshare.data.remote

import com.example.foodshare.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // FORCE local LAN base URL as requested (for device testing).
    // If you prefer emulator (10.0.2.2) or ngrok, change this value accordingly.
    private const val BASE_URL: String = " https://naturist-gab-discharge.ngrok-free.dev"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * Create a service that automatically adds Authorization: Bearer <token> when available
     */
    fun <T> createServiceWithAuth(sessionManager: com.example.foodshare.data.local.SessionManager, serviceClass: Class<T>): T {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = sessionManager.getToken()
            val builder = original.newBuilder()
            if (!token.isNullOrEmpty()) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofitAuth = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitAuth.create(serviceClass)
    }
}