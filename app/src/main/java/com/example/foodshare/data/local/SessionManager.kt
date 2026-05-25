package com.example.foodshare.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * SessionManager stores session-related values. For now this uses regular SharedPreferences
 * to remain buildable in all environments. For production, replace this with
 * EncryptedSharedPreferences or an encrypted DataStore implementation.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("foodshare_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserJson(userJson: String) {
        prefs.edit().putString(KEY_USER_JSON, userJson).apply()
    }

    fun getUserJson(): String? = prefs.getString(KEY_USER_JSON, null)

    fun saveLastScreen(route: String) {
        prefs.edit().putString(KEY_LAST_SCREEN, route).apply()
    }

    fun getLastScreen(): String? = prefs.getString(KEY_LAST_SCREEN, null)

    fun clearSession() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER_JSON).remove(KEY_LAST_SCREEN).apply()
    }

    companion object {
        private const val KEY_TOKEN = "user_token"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_LAST_SCREEN = "last_screen"
    }
}