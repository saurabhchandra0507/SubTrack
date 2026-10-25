package com.example.subscriptiontracker.ui.auth

import android.util.Patterns

// Each function returns an error message, or null if the input is fine
object Validators {

    fun name(value: String): String? =
        if (value.isBlank()) "Enter your name" else null

    fun email(value: String): String? = when {
        value.isBlank() -> "Enter your email"
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> "Enter a valid email"
        else -> null
    }

    fun newPassword(value: String): String? = when {
        value.length < 6 -> "Use at least 6 characters"
        value.none { it.isDigit() } -> "Add at least one number"
        else -> null
    }

    fun confirmPassword(password: String, confirm: String): String? =
        if (password != confirm) "Passwords don't match" else null
}
