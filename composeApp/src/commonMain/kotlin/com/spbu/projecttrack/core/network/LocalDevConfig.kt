package com.spbu.projecttrack.core.network

/**
 * Конфигурация для локальной разработки
 * 
 * ВНИМАНИЕ: Этот файл содержит настройки для разработки!
 * Измените LOCAL_MACHINE_IP на IP адрес вашего компьютера.
 */
object LocalDevConfig {
    /**
     * IP адрес вашего компьютера в локальной сети
     * 
     * Как узнать:
     * - macOS/Linux: ifconfig | grep "inet " | grep -v 127.0.0.1
     * - Windows: ipconfig | findstr "IPv4"
     */
    const val LOCAL_MACHINE_IP = "192.168.1.100"  // TODO: Замените на ваш IP
    
    /**
     * Автоматически определять IP (если возможно)
     * Если true, будет использоваться автоопределение
     * Если false, будет использоваться LOCAL_MACHINE_IP
     */
    const val AUTO_DETECT_IP = false
}

