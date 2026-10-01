package com.kinetix.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kinetix.app.data.models.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.authDataStore by preferencesDataStore(
    name = "kinetix_auth_preferences"
)

class UserPreferences(
    private val context: Context
) {
    private object Keys {
        val userId = stringPreferencesKey("user_id")
        val email = stringPreferencesKey("email")
        val firstName = stringPreferencesKey("first_name")
        val lastName = stringPreferencesKey("last_name")
        val token = stringPreferencesKey("token")
        val isAuthenticated = booleanPreferencesKey(
            "is_authenticated"
        )
    }

    val authSession: Flow<AuthResponse?> =
        context.authDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(
                        androidx.datastore.preferences.core
                            .emptyPreferences()
                    )
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val token = preferences[Keys.token]
                    ?.takeIf { it.isNotBlank() }

                val userId = preferences[Keys.userId]
                    ?.takeIf { it.isNotBlank() }

                val email = preferences[Keys.email]
                    ?.takeIf { it.isNotBlank() }

                if (
                    token == null ||
                    userId == null ||
                    email == null ||
                    preferences[Keys.isAuthenticated] != true
                ) {
                    null
                } else {
                    AuthResponse(
                        userId = userId,
                        email = email,
                        firstName =
                            preferences[Keys.firstName].orEmpty(),
                        lastName =
                            preferences[Keys.lastName].orEmpty(),
                        token = token
                    )
                }
            }

    suspend fun saveSession(
        response: AuthResponse
    ) {
        context.authDataStore.edit { preferences ->
            preferences[Keys.userId] = response.userId
            preferences[Keys.email] = response.email
            preferences[Keys.firstName] = response.firstName
            preferences[Keys.lastName] = response.lastName
            preferences[Keys.token] = response.token
            preferences[Keys.isAuthenticated] = true
        }
    }

    suspend fun clearSession() {
        context.authDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getToken(): String? {
        var token: String? = null

        context.authDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(
                        androidx.datastore.preferences.core
                            .emptyPreferences()
                    )
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[Keys.token]
            }
            .collect { storedToken ->
                token = storedToken
            }

        return token
    }
}