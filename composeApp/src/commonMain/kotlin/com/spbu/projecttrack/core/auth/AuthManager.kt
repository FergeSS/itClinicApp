package com.spbu.projecttrack.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Менеджер авторизации для управления токенами
 * 
 * Использование:
 * - AuthManager.setToken(token) - установить токен после авторизации
 * - AuthManager.clearToken() - очистить токен при выходе
 * - AuthManager.isAuthorized - проверить статус авторизации
 * - AuthManager.getToken() - получить текущий токен
 */
object AuthManager {
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()
    
    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()
    
    /**
     * Установить токен авторизации
     * @param token JWT токен
     */
    fun setToken(token: String) {
        _authToken.value = token
        _isAuthorized.value = true
    }
    
    /**
     * Очистить токен (выход из системы)
     */
    fun clearToken() {
        _authToken.value = null
        _isAuthorized.value = false
    }
    
    /**
     * Получить текущий токен
     * @return JWT токен или null
     */
    fun getToken(): String? = _authToken.value
    
    /**
     * Установить тестовый токен для разработки
     * TODO: Удалить перед релизом!
     * 
     * ВНИМАНИЕ: Замените на ваш тестовый токен для локальной разработки
     * Сгенерируйте токен на jwt.io с вашим TOKEN_SECRET из бэкенда
     */
    fun setTestToken() {
        // TODO: Замените на ваш тестовый токен
        // Сгенерируйте на https://jwt.io с payload {"id": 1} и вашим TOKEN_SECRET
        val testToken = "YOUR_TEST_TOKEN_HERE"
        setToken(testToken)
    }
}

