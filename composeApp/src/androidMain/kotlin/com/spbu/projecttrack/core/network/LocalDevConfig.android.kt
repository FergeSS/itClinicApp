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
     * Fallback IP адрес (если автоопределение не сработает)
     * TODO: Обновите на ваш текущий IP если автоопределение не работает
     * Узнать IP: ifconfig | grep "inet " | grep -v 127.0.0.1
     */
    private const val FALLBACK_IP = "192.168.1.100"  // TODO: Замените на ваш IP
    
    /**
     * Получить IP адрес компьютера в локальной сети
     * Автоматически определяет текущий IP
     */
    fun getHostIP(): String {
        return try {
            // Ищем активный сетевой интерфейс с IPv4 адресом
            val interfaces = NetworkInterface.getNetworkInterfaces()
            
            for (networkInterface in interfaces) {
                // Пропускаем loopback и неактивные интерфейсы
                if (networkInterface.isLoopback || !networkInterface.isUp) {
                    continue
                }
                
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    // Берем только IPv4 адреса, не loopback и не link-local
                    if (address is Inet4Address && 
                        !address.isLoopbackAddress && 
                        !address.isLinkLocalAddress) {
                        val ip = address.hostAddress
                        
                        // ВАЖНО: Пропускаем весь диапазон 10.0.2.x (это сеть эмулятора, не хост!)
                        if (ip != null && !ip.startsWith("10.0.2.")) {
                            println("🌐 Автоопределение IP: $ip (интерфейс: ${networkInterface.name})")
                            return ip
                        } else if (ip != null) {
                            println("⏭️  Пропущен IP эмулятора: $ip")
                        }
                    }
                }
            }
            
            println("⚠️  IP не найден, использую fallback: $FALLBACK_IP")
            FALLBACK_IP
        } catch (e: Exception) {
            println("❌ Ошибка определения IP: ${e.message}")
            FALLBACK_IP
        }
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

