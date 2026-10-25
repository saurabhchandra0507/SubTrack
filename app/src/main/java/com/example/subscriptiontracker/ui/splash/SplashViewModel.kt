package com.example.subscriptiontracker.ui.splash

import androidx.lifecycle.ViewModel
import com.example.subscriptiontracker.data.AuthRepository
import kotlinx.coroutines.flow.first

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // Read the saved login status once
    suspend fun isLoggedIn(): Boolean = authRepository.isLoggedIn.first()
}
