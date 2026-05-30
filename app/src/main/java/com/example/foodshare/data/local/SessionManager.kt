package com.example.foodshare.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("foodshare_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("user_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("user_token", null)
    }
}