package org.sake_hack.feature.auth.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * iOS用DataStoreの作成関数
 */
@OptIn(ExperimentalForeignApi::class)
private fun createDataStore(): DataStore<Preferences> {
    return createDataStore(
        produceFile = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory).path + "/auth_tokens.preferences_pb"
        }
    )
}

/**
 * iOS用TokenManager実装
 */
actual class TokenManager {
    private val dataStore = createDataStore()
    private val TOKEN_KEY = stringPreferencesKey("id_token")

    actual suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    actual suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    actual suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}

/**
 * DataStore作成用のヘルパー関数
 *
 * TODO: androidx.datastore:datastore-preferences の実際のiOS実装に置き換える
 */
private expect fun createDataStore(
    produceFile: () -> String
): DataStore<Preferences>
