package com.example.foodshare.data.remote

import com.example.foodshare.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⚠️ IMPORTANT: Choisir l'URL selon l'environnement
    // - Émulateur Android: "http://10.0.2.2:8080/"
    // - Device réel/Téléphone: "http://192.168.1.50:8080/"
    private const val BASE_URL = "https://naturist-gab-discharge.ngrok-free.dev"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}