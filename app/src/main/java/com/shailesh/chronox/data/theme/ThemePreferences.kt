package com.shailesh.chronox.data.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppTheme {
    MIDNIGHT_BLACK,
    OCEAN_BLUE,
    EMERALD_GREEN,
    SUNSET_ORANGE,
    RUBY_RED,
    ARCTIC_WHITE
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeRepository(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val AMOLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("amoled_mode")

    val themeFlow: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.MIDNIGHT_BLACK.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.MIDNIGHT_BLACK
            }
        }

    val amoledFlow: Flow<Boolean> = context.dataStore.data
        .map { it[AMOLED_KEY] ?: false }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AMOLED_KEY] = enabled
        }
    }
}
