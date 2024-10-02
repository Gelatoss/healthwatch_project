package com.healthwatch.healthapp.model

import android.content.SharedPreferences
import android.content.Context


class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN_KEY = "AuthToken"
        private const val LOGIN_STATE_KEY = "isLoggedIn"
        private const val LOGIN_TIMESTAMP_KEY = "loginTimestamp"
        private const val USER_EMAIL = "UserEmail"
        private const val USER_PASSWORD = "UserPassword"
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

    fun clearAuthToken() {
        sharedPreferences.edit().remove(AUTH_TOKEN_KEY).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(LOGIN_STATE_KEY, isLoggedIn)
        editor.putLong(LOGIN_TIMESTAMP_KEY, System.currentTimeMillis())
        editor.apply()
    }

    fun isUserLoggedIn(context: Context, durationInHours: Int) : Boolean {
        val isLoggedIn = sharedPreferences.getBoolean(LOGIN_STATE_KEY, false)
        val loginTimestamp = sharedPreferences.getLong(LOGIN_TIMESTAMP_KEY, 0L)
        val currentTime = System.currentTimeMillis()
        val allwoedDuration = durationInHours * 60 * 60 * 1000

        return isLoggedIn && (currentTime - loginTimestamp) < allwoedDuration
    }

    fun clearLoginState(context: Context) {
        sharedPreferences
            .edit()
            .remove(LOGIN_STATE_KEY)
            .remove(LOGIN_TIMESTAMP_KEY)
            .apply()
    }
}