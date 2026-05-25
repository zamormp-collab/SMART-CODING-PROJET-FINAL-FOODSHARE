package com.example.foodshare.data.remote

import com.example.foodshare.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Determine debug via reflection to avoid hard dependency on generated BuildConfig during static analysis
    private val IS_DEBUG: Boolean = try {
        val cls = Class.forName("com.example.foodshare.BuildConfig")
        val field = cls.getField("DEBUG")
        field.getBoolean(null)
    } catch (e: Exception) {
        // If BuildConfig isn't available (static checks), assume debug to favor local development
        true
    }

    // Use a sensible default depending on build type. For emulator use 10.0.2.2
    private val BASE_URL: String = if (IS_DEBUG) {
        "http://10.0.2.2:8080/"
    } else {
        "https://your.production.api/"
    }

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