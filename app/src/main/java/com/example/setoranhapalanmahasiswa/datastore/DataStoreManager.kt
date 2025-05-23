package com.example.setoranhapalanmahasiswa.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Ekstensi DataStore

// Ekstensi untuk DataStore

private val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreManager @Inject constructor(private val context: Context) {

    // Key untuk token, refresh token, dan data profil

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NIM_KEY = stringPreferencesKey("nim")
    }

    // =
    // === SAVE FUNCTIONS ===
    // =

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { it[REFRESH_TOKEN_KEY] = refreshToken }
    }

    suspend fun saveUserProfile(name: String, email: String, nim: String) {
        context.dataStore.edit {
            it[NAME_KEY] = name
            it[EMAIL_KEY] = email
            it[NIM_KEY] = nim
        }
    }

    // =
    // === READ FUNCTIONS ===
    // =

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }.firstOrNull()
    }

    val userName: Flow<String?> = context.dataStore.data.map { it[NAME_KEY] }

    val userEmail: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }

    val userNim: Flow<String?> = context.dataStore.data.map { it[NIM_KEY] }

    // ==
    // === CLEAR FUNCTIONS ===
    // ==

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    suspend fun clearRefreshToken() {
        context.dataStore.edit { it.remove(REFRESH_TOKEN_KEY) }
    }

    suspend fun clearUserProfile() {
        context.dataStore.edit {
            it.remove(NAME_KEY)
            it.remove(EMAIL_KEY)
            it.remove(NIM_KEY)
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { it.clear() }

        // Menyimpan token ke DataStore
        suspend fun saveToken(token: String) {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
            }
        }

        // Mengambil token dari DataStore
        val token: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[TOKEN_KEY]
            }

        // Menyimpan refresh token ke DataStore
        suspend fun saveRefreshToken(refreshToken: String) {
            context.dataStore.edit { preferences ->
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }

        // Mengambil refresh token dari DataStore
        suspend fun getRefreshToken(): String? {
            return context.dataStore.data
                .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
                .firstOrNull()
        }

        // Menyimpan data profil ke DataStore
        suspend fun saveUserProfile(name: String, email: String, nim: String) {
            context.dataStore.edit { preferences ->
                preferences[NAME_KEY] = name
                preferences[EMAIL_KEY] = email
                preferences[NIM_KEY] = nim
            }
        }

        // Mengambil nama dari DataStore
        val userName: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[NAME_KEY]
            }

        // Mengambil email dari DataStore
        val userEmail: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[EMAIL_KEY]
            }

        // Mengambil NIM dari DataStore
        val userNim: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[NIM_KEY]
            }

        // Hapus semua data
        suspend fun clearData() {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }

        }
    }
}