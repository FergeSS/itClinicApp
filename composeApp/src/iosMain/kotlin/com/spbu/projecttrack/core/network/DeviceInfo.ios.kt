package com.spbu.projecttrack.core.network

import platform.UIKit.UIDevice

actual object DeviceInfo {
    actual fun isEmulator(): Boolean {
        // Проверяем, это симулятор или реальное устройство
        // В симуляторе model содержит "Simulator"
        return UIDevice.currentDevice.model.contains("Simulator", ignoreCase = true)
    }
    
    actual fun getLocalHostAddress(): String {
        return if (isEmulator()) {
            // Для iOS симулятора - localhost работает напрямую
            "localhost"
        } else {
            // Для реального устройства - используем настроенный IP
            LocalDevConfig.LOCAL_MACHINE_IP
        }
    }
}

