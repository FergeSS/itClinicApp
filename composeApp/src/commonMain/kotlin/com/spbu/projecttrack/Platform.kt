package com.spbu.projecttrack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform