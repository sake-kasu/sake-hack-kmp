package org.sake_hack.feature.auth.config

actual object AuthConfig {
    actual val googleClientId: String =
        System.getenv("GOOGLE_CLIENT_ID_DEV")
            ?: System.getenv("GOOGLE_CLIENT_ID_PROD")
            ?: ""

    actual val redirectUri: String = "com.sake_hack:/oauth2redirect"
}
