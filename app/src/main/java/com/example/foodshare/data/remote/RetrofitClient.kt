package com.example.foodshare.data.remote

import android.util.Log
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // ⚠️ IMPORTANT: Choisir l'URL selon l'environnement
    // - Émulateur Android: "http://10.0.2.2:8080/"
    // - Device réel/Téléphone: "http://192.168.1.50:8080/"
    private const val BASE_URL = "https://naturist-gab-discharge.ngrok-free.dev"
    private const val TAG = "RetrofitClient"

    // OkHttpClient principal avec timeouts configurés
    private val baseHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(baseHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * Create a service that automatically adds Authorization: Bearer <token> when available
     */
    fun <T> createServiceWithAuth(sessionManager: SessionManager, serviceClass: Class<T>): T {
        val authInterceptor = Interceptor { chain ->
            val token = sessionManager.getToken()
            val builder = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofitAuth = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitAuth.create(serviceClass)
    }
}