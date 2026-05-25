package com.example.foodshare.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

/**
 * SessionManager uses EncryptedSharedPreferences when available to securely store token and user info.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences

    private val gson = Gson()

    init {
        prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "foodshare_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to plain SharedPreferences if security library unavailable
            context.getSharedPreferences("foodshare_prefs", Context.MODE_PRIVATE)
        }
    }

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