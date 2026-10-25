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

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val formError: String? = null,
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
)

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, nameError = null, formError = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, formError = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, passwordError = null, formError = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null, formError = null) }

    fun signUp() {
        val state = _uiState.value
        val errors = state.copy(
            nameError = Validators.name(state.name),
            emailError = Validators.email(state.email),
            passwordError = Validators.newPassword(state.password),
            confirmPasswordError = Validators.confirmPassword(state.password, state.confirmPassword),
        )
        val hasErrors = listOf(errors.nameError, errors.emailError, errors.passwordError, errors.confirmPasswordError)
            .any { it != null }
        if (hasErrors) {
            _uiState.value = errors
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = authRepository.signUp(state.name, state.email, state.password)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSignedUp = true) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, formError = result.message) }
            }
        }
    }
}
