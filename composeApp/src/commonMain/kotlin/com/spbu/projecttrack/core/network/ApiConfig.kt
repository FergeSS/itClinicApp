package com.spbu.projecttrack.core.network

/**
 * Конфигурация API эндпоинтов
 * 
 * Для переключения между локальным и продакшн бэкендом измените USE_LOCAL_API
 */
object ApiConfig {
    // TODO: Установите в false для использования продакшн API
    // IP адрес теперь определяется автоматически!
    private const val USE_LOCAL_API = true
    
    // Продакшн бэкенд (публичный, без авторизации для большинства эндпоинтов)
    private const val PRODUCTION_BASE_URL = "https://citec.spb.ru/api"
    
    // Порт локального бэкенда
    private const val LOCAL_PORT = 8000
    
    /**
     * Текущий базовый URL API
     * Автоматически определяет правильный адрес для эмулятора/реального устройства
     */
    val baseUrl: String
        get() = if (USE_LOCAL_API) {
            // Автоматически определяем адрес в зависимости от типа устройства
            val host = DeviceInfo.getLocalHostAddress()
            "http://$host:$LOCAL_PORT"
        } else {
            PRODUCTION_BASE_URL
        }
    
    /**
     * Используется ли локальный API
     */
    val isLocalApi: Boolean
        get() = USE_LOCAL_API
    
    /**
     * Информация о текущем устройстве и настройках
     */
    fun getDebugInfo(): String {
        return buildString {
            appendLine("=== API Configuration ===")
            appendLine("Use Local API: $USE_LOCAL_API")
            appendLine("Is Emulator: ${DeviceInfo.isEmulator()}")
            appendLine("Local Host: ${DeviceInfo.getLocalHostAddress()}")
            appendLine("Base URL: $baseUrl")
            appendLine("=======================")
        }
    }
    
    /**
     * Эндпоинты, требующие авторизации (работают только с токеном)
     */
    object AuthRequired {
        // User endpoints
        const val USER_PROJECT_STATUS = "/user/project-status"
        const val USER_PROFILE = "/user/profile"
        const val USER_ME = "/user/me"
        
        // Request endpoints
        const val REQUEST_CREATE = "/request"
        const val REQUEST_EDIT = "/request"
        const val REQUEST_AVAILABLE = "/request/available"
        const val REQUEST_DELETE = "/request/{id}"
        
        // Project results endpoints
        const val PROJECT_RESULTS_CHANGE = "/project/results/change-file"
        const val PROJECT_RESULTS_DELETE = "/project/results/delete-file"
        const val PROJECT_RESULTS_UPLOAD = "/project/results/upload-file"
        
        // Project links endpoints
        const val PROJECT_LINKS_ADD = "/project/links"
        const val PROJECT_LINKS_DELETE = "/project/links/{id}"
        
        // Profile endpoints
        const val PROFILE_EDIT_ACCOUNT = "/profile/account"
        const val PROFILE_EDIT_PERSONAL = "/profile/personal"
        
        // Member endpoints
        const val MEMBER_EDIT = "/member"
    }
    
    /**
     * Публичные эндпоинты (не требуют авторизации)
     */
    object Public {
        const val PROJECT_ACTIVE = "/project/active"
        const val PROJECT_NEW = "/project/new"
        const val PROJECT_FINDMANY = "/project/findmany"
        const val PROJECT_DETAIL = "/project/{slug}"
        const val TAGS_ALL = "/tag"
        const val EMAIL_SEND_REQUEST = "/email/send-request"
    }
}

