package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    background = Background,
    surface = Surface,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = Error,
    errorContainer = ErrorContainer,
    onError = OnError,
    outline = Outline,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryContainer,
    onPrimary = Primary,
    primaryContainer = Primary,
    secondary = SecondaryContainer,
    onSecondary = Secondary,
    secondaryContainer = Secondary,
    tertiary = TertiaryContainer,
    tertiaryContainer = Tertiary,
    background = OnBackground,
    surface = OnSurface,
    onBackground = Background,
    onSurface = Background,
    error = ErrorContainer,
    errorContainer = Error,
    onError = OnError,
    outline = Outline,
    surfaceVariant = OnSurfaceVariant,
    onSurfaceVariant = SurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
