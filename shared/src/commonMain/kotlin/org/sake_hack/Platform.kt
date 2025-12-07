package org.sake_hack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform