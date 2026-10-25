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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Redraws this screen whenever the ViewModel's state changes
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // When login succeeds, tell the nav graph to move on
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()                          // move up when the keyboard opens
            .verticalScroll(rememberScrollState()) // scroll on small phones
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        AuthHeader(
            title = "Welcome back 👋",
            subtitle = "Log in to keep track of your subscriptions"
        )
        Spacer(Modifier.height(36.dp))

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
            label = "Password",
            leadingIcon = Icons.Rounded.Lock,
            error = state.passwordError,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = viewModel::login,
        )

        state.formError?.let {
            Spacer(Modifier.height(12.dp))
            FormErrorBanner(it)
        }

        Spacer(Modifier.height(28.dp))
        PrimaryButton(text = "Log in", onClick = viewModel::login, isLoading = state.isLoading)
        Spacer(Modifier.height(12.dp))
        AuthFooterLink(question = "Don't have an account?", action = "Sign up", onClick = onSignUpClick)
    }
}
