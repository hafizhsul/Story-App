package com.example.storyapp.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference (val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

    suspend fun saveSession(name: String, userId: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[USERID_KEY] = userId
            preferences[TOKEN_KEY] = token
        }
    }

    fun getSession(): Flow<LoginResult> {
        return context.dataStore.data.map { preferences ->
            LoginResult(
                preferences[NAME_KEY] ?: "",
                preferences[USERID_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
            )
        }
    }

    suspend fun userLogout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERID_KEY = stringPreferencesKey("userId")

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }
}