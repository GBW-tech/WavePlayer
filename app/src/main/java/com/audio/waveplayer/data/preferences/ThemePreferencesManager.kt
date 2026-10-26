package com.audio.waveplayer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wave_player_preferences")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED
}

enum class ThemeDesignStyle {
    MATERIAL_EXPRESSIVE, LIQUID_GLASS
}

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    val customAccentColor: Long? = null,
    val designStyle: ThemeDesignStyle = ThemeDesignStyle.MATERIAL_EXPRESSIVE
)

class ThemePreferencesManager(private val context: Context) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val USE_DYNAMIC_COLOR_KEY = booleanPreferencesKey("use_dynamic_color")
        private val CUSTOM_ACCENT_COLOR_KEY = longPreferencesKey("custom_accent_color")
        private val DESIGN_STYLE_KEY = stringPreferencesKey("design_style")
    }

    val themeSettingsFlow: Flow<ThemeSettings> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        val mode = try {
            ThemeMode.valueOf(modeStr)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
        val useDynamic = preferences[USE_DYNAMIC_COLOR_KEY] ?: true
        val customAccent = preferences[CUSTOM_ACCENT_COLOR_KEY]
        
        val styleStr = preferences[DESIGN_STYLE_KEY] ?: ThemeDesignStyle.MATERIAL_EXPRESSIVE.name
        val designStyle = try {
            ThemeDesignStyle.valueOf(styleStr)
        } catch (_: Exception) {
            ThemeDesignStyle.MATERIAL_EXPRESSIVE
        }

        ThemeSettings(
            themeMode = mode,
            useDynamicColor = useDynamic,
            customAccentColor = if (customAccent == -1L || customAccent == null) null else customAccent,
            designStyle = designStyle
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun setUseDynamicColor(useDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_DYNAMIC_COLOR_KEY] = useDynamic
        }
    }

    suspend fun setCustomAccentColor(color: Long?) {
        context.dataStore.edit { preferences ->
            if (color == null) {
                preferences[CUSTOM_ACCENT_COLOR_KEY] = -1L
            } else {
                preferences[CUSTOM_ACCENT_COLOR_KEY] = color
            }
        }
    }

    suspend fun setDesignStyle(style: ThemeDesignStyle) {
        context.dataStore.edit { preferences ->
            preferences[DESIGN_STYLE_KEY] = style.name
        }
    }
}
