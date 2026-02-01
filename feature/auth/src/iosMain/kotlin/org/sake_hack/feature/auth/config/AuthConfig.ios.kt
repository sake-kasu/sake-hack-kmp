package org.sake_hack.feature.auth.config

import platform.Foundation.NSBundle

actual object AuthConfig {
    actual val googleClientId: String = NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_CLIENT_ID") as? String
        ?: error("GOOGLE_CLIENT_ID not configured in Info.plist")

    actual val redirectUri: String = "com.sake_hack:/oauth2redirect"
}
