package com.example.subscriptiontracker.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.subscriptiontracker.SubTrackApp
import com.example.subscriptiontracker.ui.addedit.AddEditViewModel
import com.example.subscriptiontracker.ui.auth.LoginViewModel
import com.example.subscriptiontracker.ui.auth.SignUpViewModel
import com.example.subscriptiontracker.ui.dashboard.DashboardViewModel
import com.example.subscriptiontracker.ui.insights.InsightsViewModel
import com.example.subscriptiontracker.ui.profile.ProfileViewModel
import com.example.subscriptiontracker.ui.splash.SplashViewModel

// Tells Compose how to build each ViewModel and which repository to give it.
// Use it on a screen like: viewModel(factory = AppViewModelProvider.Factory)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SplashViewModel(app().container.authRepository) }
        initializer { LoginViewModel(app().container.authRepository) }
        initializer { SignUpViewModel(app().container.authRepository) }
        initializer {
            ProfileViewModel(
                app().container.authRepository,
                app().container.settingsRepository,
                app().container.subscriptionRepository,
                app().container.reminderScheduler,
            )
        }
        initializer { InsightsViewModel(app().container.subscriptionRepository) }
        initializer { DashboardViewModel(app().container.subscriptionRepository, app().container.authRepository) }
        // createSavedStateHandle() gives the ViewModel the route's arguments, like the id
        initializer { AddEditViewModel(createSavedStateHandle(), app().container.subscriptionRepository) }
    }
}

private fun CreationExtras.app(): SubTrackApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SubTrackApp
