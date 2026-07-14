package com.example.myapplication.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val CORREO_KEY = stringPreferencesKey("correo")
        private val NOMBRES_KEY = stringPreferencesKey("nombres")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val ROLES_KEY = stringSetPreferencesKey("roles")
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val roles: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[ROLES_KEY] ?: emptySet()
    }

    suspend fun saveSession(token: String, correo: String, nombres: String, userId: Long, roles: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[CORREO_KEY] = correo
            preferences[NOMBRES_KEY] = nombres
            preferences[USER_ID_KEY] = userId
            preferences[ROLES_KEY] = roles.toSet()
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
