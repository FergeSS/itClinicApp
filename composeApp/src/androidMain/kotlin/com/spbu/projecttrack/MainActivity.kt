package com.spbu.projecttrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.spbu.projecttrack.core.storage.initAppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Инициализируем AppPreferences для Android
        initAppPreferences(this)

        setContent {
            App()
        }
    }
}

