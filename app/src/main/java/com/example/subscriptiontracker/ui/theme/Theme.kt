package com.example.subscriptiontracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    secondary = Mint,
    onSecondary = Color(0xFF00382A),
    tertiary = Amber,
    error = Coral,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkBackground,
    onSurface = DarkOnSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = Color(0xFF34344F),
    outlineVariant = Color(0xFF26263D),
)

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    secondary = Color(0xFF00B386),
    onSecondary = Color.White,
    tertiary = Amber,
    error = Coral,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightBackground,
    onSurface = LightOnSurface,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = Color(0xFFD9D7E6),
    outlineVariant = Color(0xFFEDEBF7),
)

// Rounded corners used across the app
val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun SubscriptionTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic colour (wallpaper-based) is turned off so our brand colours always show
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
