package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = CoralPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A0A1C),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF33206B),
    onSecondaryContainer = Color(0xFFEADBFF),
    tertiary = NeonCyan,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004D5A),
    onTertiaryContainer = Color(0xFFBCEEFF),
    background = StudioDarkBg,
    onBackground = TextPrimaryDark,
    surface = StudioSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = StudioCardBorder,
    outlineVariant = Color(0xFF28253B)
)

private val LightColorScheme = lightColorScheme(
    primary = CoralPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF4A0A1C),
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADBFF),
    onSecondaryContainer = Color(0xFF230E57),
    tertiary = Color(0xFF0097A7),
    onTertiary = Color.White,
    background = StudioLightBg,
    onBackground = TextPrimaryLight,
    surface = StudioSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F3F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = StudioCardBorderLight
)

@Composable
fun VideoCraftTheme(
    darkTheme: Boolean = true, // Default to sleek studio dark theme for cinema experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
