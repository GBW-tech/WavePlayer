package com.audio.waveplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.audio.waveplayer.data.preferences.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = WavePrimaryLight,
    onPrimary = WaveOnPrimaryLight,
    primaryContainer = WavePrimaryContainerLight,
    onPrimaryContainer = WaveOnPrimaryContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = WavePrimaryDark,
    onPrimary = WaveOnPrimaryDark,
    primaryContainer = WavePrimaryContainerDark,
    onPrimaryContainer = WaveOnPrimaryContainerDark
)

private val AmoledColorScheme = darkColorScheme(
    primary = WavePrimaryDark,
    onPrimary = WaveOnPrimaryDark,
    primaryContainer = WavePrimaryContainerDark,
    onPrimaryContainer = WaveOnPrimaryContainerDark,
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurfaceVariant
)

@Composable
fun WavePlayerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    useDynamicColor: Boolean = true,
    customAccentColor: Long? = null,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }

    val context = LocalContext.current
    val dynamicColor = useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val baseColorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        themeMode == ThemeMode.AMOLED -> AmoledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colorScheme = if (customAccentColor != null) {
        val customColor = Color(customAccentColor)
        baseColorScheme.copy(
            primary = customColor,
            primaryContainer = customColor.copy(alpha = 0.3f)
        )
    } else {
        baseColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
