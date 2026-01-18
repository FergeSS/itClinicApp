package com.spbu.projecttrack

/**
 * ШАБЛОН конфигурации сборки
 * 
 * 📋 ИНСТРУКЦИЯ:
 * 1. Скопируйте этот файл как BuildConfig.kt (в ту же папку)
 * 2. Заполните своими значениями
 * 3. BuildConfig.kt уже добавлен в .gitignore и не будет коммититься
 * 
 * Для других разработчиков: используйте этот файл как шаблон!
 */
object BuildConfigExample {
    /**
     * Тестовый токен для локальной разработки
     * Получите токен, запустив: node generate-test-token.js в папке Registry/
     */
    const val TEST_TOKEN = "your_test_token_here"
    
    /**
     * Использовать локальный API (true) или продакшн (false)
     */
    const val USE_LOCAL_API = true
    
    /**
     * URL продакшн API
     */
    const val PRODUCTION_BASE_URL = "https://citec.spb.ru/api"
    
    /**
     * Порт локального API
     */
    const val LOCAL_PORT = 8000
    
    /**
     * GitHub OAuth Client ID (если используется)
     */
    const val GITHUB_CLIENT_ID = "your_github_client_id"
    
    /**
     * GitHub OAuth Client Secret (если используется)
     */
    const val GITHUB_CLIENT_SECRET = "your_github_client_secret"
}
