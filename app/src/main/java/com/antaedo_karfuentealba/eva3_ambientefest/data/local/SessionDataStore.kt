package com.antaedo_karfuentealba.eva3_ambientefest.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Session

// Extensión para crear el DataStore
private val Context.sessionDataStore by preferencesDataStore(name = "session_prefs")

class SessionDataStore(private val context: Context) {
    private val dataStore = context.sessionDataStore

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_ROLE_ID = intPreferencesKey("role_id")
    }

    // Flujo que expone la sesión actual
    val sessionFlow: Flow<Session> = dataStore.data.map { prefs ->
        Session(
            token = prefs[KEY_TOKEN],
            userId = prefs[KEY_USER_ID],
            name = prefs[KEY_NAME],
            email = prefs[KEY_EMAIL],
            roleId = prefs[KEY_ROLE_ID]
        )
    }

    // Guardar sesión después de login/registro
    suspend fun saveSession(token: String?, userId: Int?, email: String?, roleId: Int?, name: String?) {
        dataStore.edit { prefs ->
            token?.let { prefs[KEY_TOKEN] = it }
            userId?.let { prefs[KEY_USER_ID] = it }
            email?.let { prefs[KEY_EMAIL] = it }
            roleId?.let { prefs[KEY_ROLE_ID] = it }
            name?.let { prefs[KEY_NAME] = it }
        }
    }

    // Limpiar sesión (logout)
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}