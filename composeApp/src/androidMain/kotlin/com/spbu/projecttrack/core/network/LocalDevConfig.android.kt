package com.spbu.projecttrack.core.network

import java.net.NetworkInterface
import java.net.Inet4Address

/**
 * Конфигурация для локальной разработки (Android)
 * 
 * Автоматически определяет IP адрес компьютера в локальной сети
 */
object LocalDevConfig {
    /**
     * Получить IP адрес устройства в локальной сети
     */
    private fun getDeviceIP(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            
            for (networkInterface in interfaces) {
                if (networkInterface.isLoopback || !networkInterface.isUp) {
                    continue
                }
                
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (address is Inet4Address && 
                        !address.isLoopbackAddress && 
                        !address.isLinkLocalAddress) {
                        val ip = address.hostAddress
                        
                        // Пропускаем 10.0.2.x (эмулятор)
                        if (ip != null && !ip.startsWith("10.0.2.")) {
                            println("📱 IP устройства: $ip (${networkInterface.name})")
                            return ip
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            println("❌ Ошибка получения IP устройства: ${e.message}")
            null
        }
    }
    
    /**
     * Получить возможные IP адреса компьютера на основе IP устройства
     * Возвращает список IP для проверки (обычно компьютер имеет IP .1, .2, .100-110)
     */
    private fun getHostIPCandidates(deviceIP: String): List<String> {
        val parts = deviceIP.split(".")
        if (parts.size != 4) return emptyList()
        
        val subnet = "${parts[0]}.${parts[1]}.${parts[2]}"
        val candidates = mutableListOf<String>()
        
        // Обычные адреса для компьютеров:
        // 1. Роутер обычно .1
        candidates.add("$subnet.1")
        
        // 2. Часто компьютеры получают адреса от .2 до .10
        for (i in 2..10) {
            candidates.add("$subnet.$i")
        }
        
        // 3. Диапазон .100-110 (часто используется для статических адресов)
        for (i in 100..110) {
            candidates.add("$subnet.$i")
        }
        
        // 4. Если IP устройства не в этих диапазонах, добавим соседние адреса
        val deviceLastOctet = parts[3].toIntOrNull() ?: 0
        for (offset in listOf(-2, -1, 1, 2)) {
            val newOctet = deviceLastOctet + offset
            if (newOctet in 2..254 && !candidates.contains("$subnet.$newOctet")) {
                candidates.add("$subnet.$newOctet")
            }
        }
        
        println("🔍 Кандидаты IP хоста: ${candidates.take(5)}... (всего ${candidates.size})")
        return candidates
    }
    
    /**
     * Получить IP адрес компьютера в локальной сети
     * Автоматически определяет на основе IP устройства
     */
    fun getHostIP(): String {
        return try {
            val deviceIP = getDeviceIP()
            
            if (deviceIP != null) {
                // Получаем кандидатов для проверки
                val candidates = getHostIPCandidates(deviceIP)
                
                if (candidates.isNotEmpty()) {
                    // Выбираем IP, исключая .1 (это обычно роутер, а не компьютер)
                    // Приоритет: .2-.10, потом .100-110
                    val selectedIP = candidates.firstOrNull { 
                        val octet = it.split(".")[3].toIntOrNull() ?: 0
                        octet in 2..10  // Компьютеры обычно .2-.10
                    } ?: candidates.firstOrNull {
                        val octet = it.split(".")[3].toIntOrNull() ?: 0
                        octet in 100..110  // Или статические адреса
                    } ?: candidates.first()
                    
                    println("✅ Выбран IP хоста: $selectedIP (на основе устройства $deviceIP)")
                    println("💡 Если не работает, проверьте реальный IP компьютера и настройте вручную")
                    return selectedIP
                }
            }
            
            // Fallback: .2 вместо .1 (компьютер, а не роутер)
            val fallback = "192.168.1.2"
            println("⚠️  Не удалось определить IP автоматически, использую $fallback")
            println("💡 Узнайте IP компьютера: ipconfig (Windows) или ifconfig (Mac/Linux)")
            fallback
        } catch (e: Exception) {
            println("❌ Ошибка определения IP: ${e.message}")
            "192.168.1.2"
        }
    }
    
    /**
     * Получить список всех возможных IP для проверки
     * Полезно для ручного подбора
     */
    fun getAllHostCandidates(): List<String> {
        val deviceIP = getDeviceIP() ?: return emptyList()
        return getHostIPCandidates(deviceIP)
    }
    
    /**
     * Получить информацию обо всех сетевых интерфейсах (для отладки)
     */
    fun getNetworkInfo(): String = buildString {
        try {
            appendLine("=== Сетевые интерфейсы ===")
            val interfaces = NetworkInterface.getNetworkInterfaces()
            
            for (networkInterface in interfaces) {
                appendLine("Интерфейс: ${networkInterface.name}")
                appendLine("  Активен: ${networkInterface.isUp}")
                appendLine("  Loopback: ${networkInterface.isLoopback}")
                
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (address is Inet4Address) {
                        appendLine("  IPv4: ${address.hostAddress}")
                    }
                }
                appendLine()
            }
        } catch (e: Exception) {
            appendLine("Ошибка: ${e.message}")
        }
    }
}

