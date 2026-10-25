package com.example.subscriptiontracker.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.theme.BrandGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onFinished: (isLoggedIn: Boolean) -> Unit,
    viewModel: SplashViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val logoScale = remember { Animatable(0.6f) }
    val textAlpha = remember { Animatable(0f) }

    // Runs once when the screen appears: animate, wait, check login, then move on
    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        textAlpha.animateTo(1f, tween(durationMillis = 600, delayMillis = 200))
        delay(700)
        onFinished(viewModel.isLoggedIn())
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(logoScale.value)
                    .clip(MaterialTheme.shapes.large)
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(20.dp))
            Column(Modifier.alpha(textAlpha.value), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SubTrack", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Every subscription, one place",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
