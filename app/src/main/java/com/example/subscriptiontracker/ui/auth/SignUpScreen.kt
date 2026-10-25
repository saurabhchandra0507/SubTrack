package com.example.subscriptiontracker.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.components.AppTextField
import com.example.subscriptiontracker.ui.components.PrimaryButton

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: SignUpViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSignedUp) {
        if (state.isSignedUp) onSignUpSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        AuthHeader(
            title = "Create account",
            subtitle = "Start tracking what you pay for, in a minute"
        )
        Spacer(Modifier.height(36.dp))

        AppTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = "Full name",
            leadingIcon = Icons.Rounded.Person,
            error = state.nameError,
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            leadingIcon = Icons.Rounded.Email,
            error = state.emailError,
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password (6+ characters, 1 number)",
            leadingIcon = Icons.Rounded.Lock,
            error = state.passwordError,
            isPassword = true,
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirm password",
            leadingIcon = Icons.Rounded.LockReset,
            error = state.confirmPasswordError,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = viewModel::signUp,
        )

        state.formError?.let {
            Spacer(Modifier.height(12.dp))
            FormErrorBanner(it)
        }

        Spacer(Modifier.height(28.dp))
        PrimaryButton(text = "Create account", onClick = viewModel::signUp, isLoading = state.isLoading)
        Spacer(Modifier.height(12.dp))
        AuthFooterLink(question = "Already have an account?", action = "Log in", onClick = onBackToLogin)
    }
}
