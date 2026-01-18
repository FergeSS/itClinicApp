package com.spbu.projecttrack.core.network

import platform.Foundation.*
import kotlinx.cinterop.*
import platform.posix.*

/**
 * Конфигурация для локальной разработки (iOS)
 * 
 * На iOS симуляторе используется localhost
 * На реальном устройстве автоматически определяет IP хоста
 */
object LocalDevConfig {
    /**
     * Получить IP адрес устройства в локальной сети
     */
    private fun getDeviceIP(): String? {
        return try {
            // Получаем список всех сетевых интерфейсов
            var ifaddr: CPointer<ifaddrs>? = null
            if (getifaddrs(cValuesOf(ifaddr)) == 0) {
                var ptr = ifaddr
                while (ptr != null) {
                    val interface_address = ptr.pointed
                    val addr_family = interface_address.ifa_addr?.pointed?.sa_family?.toInt()
                    
                    // Ищем IPv4 адрес (AF_INET = 2)
                    if (addr_family == AF_INET) {
                        val addr = interface_address.ifa_addr?.reinterpret<sockaddr_in>()?.pointed
                        if (addr != null) {
                            val ipBytes = addr.sin_addr.s_addr
                            val ip = "${(ipBytes and 0xFFu).toInt()}.${((ipBytes shr 8) and 0xFFu).toInt()}.${((ipBytes shr 16) and 0xFFu).toInt()}.${((ipBytes shr 24) and 0xFFu).toInt()}"
                            
                            // Пропускаем loopback
                            if (!ip.startsWith("127.") && !ip.startsWith("169.254.")) {
                                println("📱 IP устройства (iOS): $ip")
                                freeifaddrs(ifaddr)
                                return ip
                            }
                        }
                    }
                    ptr = interface_address.ifa_next
                }
                freeifaddrs(ifaddr)
            }
            null
        } catch (e: Exception) {
            println("❌ Ошибка получения IP устройства (iOS): ${e.message}")
            null
        }
    }
    
    /**
     * Получить возможные IP адреса компьютера на основе IP устройства
     */
    private fun getHostIPCandidates(deviceIP: String): List<String> {
        val parts = deviceIP.split(".")
        if (parts.size != 4) return emptyList()
        
        val subnet = "${parts[0]}.${parts[1]}.${parts[2]}"
        val candidates = mutableListOf<String>()
        
        // Обычные адреса для компьютеров
        candidates.add("$subnet.1")  // Роутер
        for (i in 2..10) {
            candidates.add("$subnet.$i")
        }
        for (i in 100..110) {
            candidates.add("$subnet.$i")
        }
        
        println("🔍 Кандидаты IP хоста (iOS): ${candidates.take(5)}... (всего ${candidates.size})")
        return candidates
    }
    
    /**
     * IP адрес компьютера (для обратной совместимости)
     */
    val LOCAL_MACHINE_IP: String
        get() = getHostIP()
    
    /**
     * Получить IP адрес компьютера
     * Автоматически определяет на основе IP устройства
     */
    fun getHostIP(): String {
        return try {
            val deviceIP = getDeviceIP()
            
            if (deviceIP != null) {
                val candidates = getHostIPCandidates(deviceIP)
                
                if (candidates.isNotEmpty()) {
                    val selectedIP = candidates.firstOrNull { 
                        it.split(".")[3].toIntOrNull()?.let { octet -> octet in 2..10 } == true 
                    } ?: candidates.first()
                    
                    println("✅ Выбран IP хоста (iOS): $selectedIP (на основе устройства $deviceIP)")
                    return selectedIP
                }
            }
            
            // Fallback
            val fallback = "192.168.1.1"
            println("⚠️  Не удалось определить IP автоматически (iOS), использую $fallback")
            fallback
        } catch (e: Exception) {
            println("❌ Ошибка определения IP (iOS): ${e.message}")
            "192.168.1.1"
        }
    }
    
    /**
     * Получить информацию о сети
     */
    fun getNetworkInfo(): String {
        val deviceIP = getDeviceIP() ?: "Не определен"
        val hostIP = getHostIP()
        return "iOS устройство: $deviceIP\nХост: $hostIP"
    }
}


