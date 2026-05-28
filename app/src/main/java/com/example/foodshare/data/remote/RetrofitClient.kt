package com.example.foodshare.data.remote

import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // ⚠️ IMPORTANT: Choisir l'URL selon l'environnement
    // - Émulateur Android: "http://10.0.2.2:8080/"
    // - Device réel/Téléphone: "http://192.168.1.50:8080/"
    private const val BASE_URL = "https://chance-casino-jumble.ngrok-free.dev/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    fun <T> createServiceWithAuth(sessionManager: SessionManager, serviceClass: Class<T>): T {
        val authInterceptor = Interceptor { chain ->
            val token = sessionManager.getToken()
            val builder = chain.request().newBuilder()
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