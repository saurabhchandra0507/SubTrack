package com.example.subscriptiontracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptiontracker.data.AuthRepository
import com.example.subscriptiontracker.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Everything the Login screen needs to draw itself
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // Private + mutable inside, public + read-only outside
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Typing clears that field's error so the red text disappears straight away
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, formError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, formError = null) }
    }

    fun login() {
        val state = _uiState.value
        val emailError = Validators.email(state.email)
        val passwordError = if (state.password.isEmpty()) "Enter your password" else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        // viewModelScope: the work is cancelled automatically if the screen is closed
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = authRepository.login(state.email, state.password)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, formError = result.message) }
            }
        }
    }
}
