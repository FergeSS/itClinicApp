package com.spbu.projecttrack.core.network

import com.spbu.projecttrack.core.auth.AuthManager
import io.ktor.client.plugins.api.*

/**
 * Плагин для автоматического добавления токена авторизации к запросам
 */
val AuthInterceptor = createClientPlugin("AuthInterceptor") {
    onRequest { request, _ ->
        // Если есть токен, добавляем его в заголовок Authorization
        AuthManager.getToken()?.let { token ->
            request.headers.append("Authorization", "Bearer $token")
        }
    }
}



