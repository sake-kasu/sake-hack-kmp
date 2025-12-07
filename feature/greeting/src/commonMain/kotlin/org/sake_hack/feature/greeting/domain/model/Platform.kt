package org.sake_hack.feature.greeting.domain.model

/**
 * Domain entity representing platform information.
 * This is the core business model for platform capabilities.
 */
interface Platform {
    val name: String
}

/**
 * Factory function to get platform instance.
 * Implementation provided by platform-specific source sets.
 */
expect fun getPlatform(): Platform
