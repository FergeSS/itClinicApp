package com.spbu.projecttrack.core.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferencesImpl(context: Context) : AppPreferences {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_preferences",
        Context.MODE_PRIVATE
    )
    
    override fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    override fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }
    
    override fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    override fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    
    override fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }
    
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}

// Глобальная переменная для хранения единственного экземпляра
private var instance: AppPreferences? = null

actual fun createAppPreferences(): AppPreferences {
    return instance ?: throw IllegalStateException(
        "AppPreferences not initialized. Call initAppPreferences(context) first."
    )
}

// Функция инициализации (вызывается в MainActivity)
fun initAppPreferences(context: Context) {
    if (instance == null) {
        instance = AppPreferencesImpl(context.applicationContext)
    }
}

