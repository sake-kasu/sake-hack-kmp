package org.sake_hack.feature.auth.config

import org.sake_hack.BuildConfig

actual object AuthConfig {
    actual val googleClientId: String = if (BuildConfig.DEBUG) {
        BuildConfig.GOOGLE_CLIENT_ID_DEV
    } else {
        BuildConfig.GOOGLE_CLIENT_ID_PROD
    }

    actual val redirectUri: String = "com.sake_hack:/oauth2redirect"
}
