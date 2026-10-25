package com.example.subscriptiontracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.subscriptiontracker.data.AppCurrency
import com.example.subscriptiontracker.data.ThemeMode
import com.example.subscriptiontracker.navigation.AppNavGraph
import com.example.subscriptiontracker.ui.LocalCurrency
import com.example.subscriptiontracker.ui.theme.SubscriptionTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = (application as SubTrackApp).container.settingsRepository

        setContent {
            // Listen to the saved settings; the whole app redraws when they change
            val themeMode by settings.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val currency by settings.currency.collectAsStateWithLifecycle(initialValue = AppCurrency.INR)

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            // Status bar icons: dark icons on the light theme, white icons on the dark theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                )
                onDispose {}
            }

            SubscriptionTrackerTheme(darkTheme = darkTheme) {
                // Every screen inside can read LocalCurrency.current
                CompositionLocalProvider(LocalCurrency provides currency) {
                    AppNavGraph()
                }
            }
        }
    }
}
