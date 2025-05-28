package com.example.pabproject.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Brown,
    secondary = LightBrown,
    tertiary = Gold,
    background = Black,
    surface = Black,
    surfaceVariant = Black.copy(alpha = 0.7f),
    primaryContainer = Brown.copy(alpha = 0.7f),
    secondaryContainer = LightBrown.copy(alpha = 0.4f),
    tertiaryContainer = Gold.copy(alpha = 0.4f),
    onPrimary = White,
    onSecondary = White,
    onTertiary = Black,
    onBackground = PaleOrange,
    onSurface = PaleOrange,
    onSurfaceVariant = PaleOrange.copy(alpha = 0.7f),
    onPrimaryContainer = White,
    onSecondaryContainer = White,
    onTertiaryContainer = Black,
    outline = LightBrown
)

private val LightColorScheme = lightColorScheme(
    primary = Brown,
    secondary = LightBrown,
    tertiary = Gold,
    background = White,
    surface = White,
    surfaceVariant = PaleYellow.copy(alpha = 0.7f),
    primaryContainer = SandyBeige,
    secondaryContainer = PaleYellow,
    tertiaryContainer = LightPeach,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Black,
    onBackground = Brown,
    onSurface = Brown,
    onSurfaceVariant = LightBrown,
    onPrimaryContainer = Brown,
    onSecondaryContainer = Brown,
    onTertiaryContainer = Brown,
    outline = LightBrown.copy(alpha = 0.5f)
)

@Composable
fun PABProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to always use custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}