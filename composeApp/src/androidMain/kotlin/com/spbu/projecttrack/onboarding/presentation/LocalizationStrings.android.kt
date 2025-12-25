package com.spbu.projecttrack.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
actual fun getLocalizedAppName(): String {
    val locale = LocalContext.current.resources.configuration.locales[0]
    return when (locale.language) {
        "ru" -> "IT Clinic"
        else -> "IT Clinic"
    }
}

@Composable
actual fun getLocalizedAuthText(): String {
    val locale = LocalContext.current.resources.configuration.locales[0]
    return when (locale.language) {
        "ru" -> "Авторизация через Git"
        else -> "Login with Git"
    }
}

@Composable
actual fun getLocalizedContinueText(): String {
    val locale = LocalContext.current.resources.configuration.locales[0]
    return when (locale.language) {
        "ru" -> "Продолжить без авторизации"
        else -> "Continue without authorization"
    }
}





