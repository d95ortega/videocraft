package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

object ApiKeyManager {
    private const val PREFS_NAME = "videocraft_studio_prefs"
    private const val KEY_GEMINI_CUSTOM = "gemini_custom_api_key"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Gets the active Gemini API key. Checks user-saved custom key first,
     * then falls back to BuildConfig.GEMINI_API_KEY if available.
     */
    fun getActiveApiKey(context: Context): String {
        val userKey = getPrefs(context).getString(KEY_GEMINI_CUSTOM, "")?.trim() ?: ""
        if (userKey.isNotEmpty() && userKey.startsWith("AIzaSy")) {
            return userKey
        }
        
        try {
            val buildKey = BuildConfig.GEMINI_API_KEY.trim()
            if (buildKey.isNotEmpty() && !buildKey.contains("MY_GEMINI_API_KEY")) {
                return buildKey
            }
        } catch (_: Throwable) {}

        return userKey
    }

    fun getCustomApiKey(context: Context): String {
        return getPrefs(context).getString(KEY_GEMINI_CUSTOM, "")?.trim() ?: ""
    }

    fun saveCustomApiKey(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_GEMINI_CUSTOM, key.trim()).apply()
    }

    fun isConfigured(context: Context): Boolean {
        val key = getActiveApiKey(context)
        return key.isNotEmpty() && !key.contains("MY_GEMINI_API_KEY")
    }
}
