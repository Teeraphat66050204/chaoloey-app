package com.example.chaoloey.util

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_USERNAME).apply()
    }

    fun saveUsername(name: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, name).apply()
    }

    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "Guest User") ?: "Guest User"
    }

    companion object {
        private const val PREF_NAME = "chaoloey_preferences"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USERNAME = "username"
    }
}
