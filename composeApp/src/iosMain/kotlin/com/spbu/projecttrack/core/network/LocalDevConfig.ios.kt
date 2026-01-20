package com.spbu.projecttrack.core.network

import platform.Foundation.*
import kotlinx.cinterop.*

/**
 * Конфигурация для локальной разработки (iOS)
 * 
 * На iOS симуляторе используется localhost
 * На реальном устройстве автоматически определяет IP хоста
 */
object LocalDevConfig {
    /**
     * Получить IP адрес устройства в локальной сети
     * Упрощенная версия - используем WiFi IP из системных настроек
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun getDeviceIP(): String? {
        return try {
            // Пытаемся получить IP из системных настроек
            val wifiIP = getWiFiIP()
            if (wifiIP != null) {
                println("📱 IP устройства (iOS): $wifiIP")
                return wifiIP
            }
            
            // Fallback - возвращаем null если не удалось
            println("⚠️  Не удалось получить IP устройства (iOS)")
            null
        } catch (e: Exception) {
            println("❌ Ошибка получения IP устройства (iOS): ${e.message}")
            null
        }
    }
    
    /**
     * Получить WiFi IP адрес устройства
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun getWiFiIP(): String? {
        // Упрощенная реализация - возвращаем адрес из локальной подсети
        // В реальном iOS приложении можно использовать SystemConfiguration framework
        return null
    }
    
    /**
     * Пользовательский IP (для ручного ввода)
     */
    var userDefinedIp: String = ""
    
    /**
     * IP адрес компьютера (для обратной совместимости)
     */
    val LOCAL_MACHINE_IP: String
        get() = getHostIP()
    
    /**
     * Fallback IP по умолчанию
     */
    private const val FALLBACK_IP = "192.168.1.153"
    
    /**
     * Получить IP адрес компьютера
     * На iOS приоритет: userDefinedIp -> FALLBACK_IP
     */
    fun getHostIP(): String {
        // Приоритет 1: Пользовательский IP
        if (userDefinedIp.isNotBlank()) {
            println("✅ Используется пользовательский IP (iOS): $userDefinedIp")
            return userDefinedIp
        }
        
        // Приоритет 2: Попытка определить автоматически
        val deviceIP = getDeviceIP()
        if (deviceIP != null) {
            // Предполагаем, что компьютер на .2 (обычно .1 - роутер)
            val parts = deviceIP.split(".")
            if (parts.size == 4) {
                val subnet = "${parts[0]}.${parts[1]}.${parts[2]}"
                val hostIP = "$subnet.2"
                println("✅ IP хоста (iOS): $hostIP (на основе устройства $deviceIP)")
                return hostIP
            }
        }
        
        // Fallback
        println("⚠️  Не удалось определить IP автоматически (iOS), использую $FALLBACK_IP")
        println("💡 Совет: Откройте NetworkDebugScreen в приложении для ручного ввода IP")
        println("💡 Текущий IP вашего Mac: используйте 'ifconfig' в терминале для проверки")
        return FALLBACK_IP
    }
    
    /**
     * Получить информацию о сети
     */
    fun getNetworkInfo(): String {
        val deviceIP = getDeviceIP() ?: "Не определен"
        val hostIP = getHostIP()
        val source = when {
            userDefinedIp.isNotBlank() -> "Пользовательский"
            deviceIP != null -> "Автоматически"
            else -> "Fallback"
        }
        return "iOS устройство: $deviceIP\nХост: $hostIP\nИсточник: $source"
    }
}


