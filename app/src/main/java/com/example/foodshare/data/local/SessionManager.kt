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

    fun saveUserJson(json: String) {
        prefs.edit().putString("user_json", json).apply()
    }

    fun getUserJson(): String? {
        return prefs.getString("user_json", null)
    }

    fun saveLastScreen(route: String) {
        prefs.edit().putString("last_screen", route).apply()
    }

    fun getLastScreen(): String? {
        return prefs.getString("last_screen", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}