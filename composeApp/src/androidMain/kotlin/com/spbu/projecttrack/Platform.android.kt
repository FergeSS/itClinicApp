package com.spbu.projecttrack

import android.os.Build
import androidx.compose.ui.tooling.preview.Preview

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()