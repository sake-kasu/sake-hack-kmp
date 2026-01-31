package org.sake_hack.feature.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStoreの拡張プロパティ
 */
private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

/**
 * Android用TokenManager実装
 */
actual class TokenManager(private val context: Context) {
    private val dataStore = context.tokenDataStore
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
