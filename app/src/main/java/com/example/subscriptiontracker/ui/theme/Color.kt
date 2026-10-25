package com.example.subscriptiontracker.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand colours (same in light and dark mode)
val Violet = Color(0xFF7C5CFF)
val VioletDeep = Color(0xFF5B3DF5)
val Mint = Color(0xFF00D9A3)
val Coral = Color(0xFFFF6B6B)
val Amber = Color(0xFFFFB547)

// Dark mode surfaces
val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF1C1C2E)
val DarkSurfaceHigh = Color(0xFF26263D)
val DarkOnSurface = Color(0xFFF4F4F8)
val DarkOnSurfaceMuted = Color(0xFF9A9AB0)

// Light mode surfaces
val LightBackground = Color(0xFFF6F6FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceHigh = Color(0xFFEDEBF7)
val LightOnSurface = Color(0xFF15151F)
val LightOnSurfaceMuted = Color(0xFF6B6B80)

// Used for the big "total spend" card on the dashboard
val BrandGradient = Brush.linearGradient(listOf(Violet, VioletDeep, Color(0xFF3A1FD1)))

// Colours a user can pick for each subscription
val SubscriptionColors = listOf(
    Color(0xFFE50914), // red (Netflix-like)
    Color(0xFF1DB954), // green (Spotify-like)
    Color(0xFF7C5CFF), // violet
    Color(0xFF00A8E1), // blue
    Color(0xFFFF9900), // orange
    Color(0xFFFF4FA3), // pink
    Color(0xFF00D9A3), // mint
    Color(0xFF8E8EA8), // grey
)
