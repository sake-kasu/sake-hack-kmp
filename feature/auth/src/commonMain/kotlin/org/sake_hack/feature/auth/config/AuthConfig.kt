package org.sake_hack.feature.auth.config

expect object AuthConfig {
    val googleClientId: String
    val redirectUri: String
}
