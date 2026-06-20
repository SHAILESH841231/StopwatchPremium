package com.shailesh.chronox.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.shailesh.chronox.data.theme.AppTheme

private fun createCustomColorScheme(
    background: Color,
    primary: Color,
    secondary: Color,
    isAmoled: Boolean = false,
    isDark: Boolean = true
): ColorScheme {
    val finalBackground = if (isAmoled && isDark) Color.Black else background
    
    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = if (finalBackground == Color.White || finalBackground == ArcticBackground) Color.White else Color.Black,
            secondary = secondary,
            onSecondary = Color.Black,
            background = finalBackground,
            onBackground = if (finalBackground == Color.White || finalBackground == ArcticBackground) Color.Black else Color.White,
            surface = finalBackground,
            onSurface = if (finalBackground == Color.White || finalBackground == ArcticBackground) Color.Black else Color.White,
            surfaceVariant = primary.copy(alpha = 0.1f),
            onSurfaceVariant = if (finalBackground == Color.White || finalBackground == ArcticBackground) Color.Black else Color.White,
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            background = finalBackground,
            surface = finalBackground,
        )
    }
}

@Composable
fun ChronoXTheme(
    appTheme: AppTheme = AppTheme.MIDNIGHT_BLACK,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.MIDNIGHT_BLACK -> createCustomColorScheme(MidnightBackground, MidnightPrimary, MidnightSecondary, isAmoled)
        AppTheme.OCEAN_BLUE -> createCustomColorScheme(OceanBackground, OceanPrimary, OceanSecondary, isAmoled)
        AppTheme.EMERALD_GREEN -> createCustomColorScheme(EmeraldBackground, EmeraldPrimary, EmeraldSecondary, isAmoled)
        AppTheme.SUNSET_ORANGE -> createCustomColorScheme(SunsetBackground, SunsetPrimary, SunsetSecondary, isAmoled)
        AppTheme.RUBY_RED -> createCustomColorScheme(RubyBackground, RubyPrimary, RubySecondary, isAmoled)
        AppTheme.ARCTIC_WHITE -> createCustomColorScheme(ArcticBackground, ArcticPrimary, ArcticSecondary, isAmoled, isDark = false)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val isLight = appTheme == AppTheme.ARCTIC_WHITE && !isAmoled
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
