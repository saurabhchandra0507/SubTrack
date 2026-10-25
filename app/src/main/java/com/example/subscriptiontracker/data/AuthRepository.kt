package com.example.subscriptiontracker.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import kotlinx.coroutines.flow.Flow

// Result of a login or sign up: either it worked, or here's why not
sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}

// The login rules live here. Screens and ViewModels never touch DataStore directly.
class AuthRepository(private val userPreferences: UserPreferences) {

    val account: Flow<UserAccount?> = userPreferences.account
    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn

    suspend fun signUp(name: String, email: String, password: String): AuthResult {
        // This app keeps one account per phone
        if (userPreferences.getCredentials() != null) {
            return AuthResult.Error("An account already exists on this phone. Log in instead.")
        }
        val salt = newSalt()
        userPreferences.saveAccount(
            name = name.trim(),
            email = email.trim().lowercase(),
            passwordHash = hash(password, salt),
            salt = salt
        )
        userPreferences.setLoggedIn(true)
        return AuthResult.Success
    }

    suspend fun login(email: String, password: String): AuthResult {
        val saved = userPreferences.getCredentials()
            ?: return AuthResult.Error("No account yet. Create one first.")

        val emailMatches = saved.email.equals(email.trim(), ignoreCase = true)
        val passwordMatches = hash(password, saved.salt) == saved.passwordHash
        if (!emailMatches || !passwordMatches) {
            // Don't say which one was wrong; that would help someone guessing
            return AuthResult.Error("Email or password is incorrect")
        }
        userPreferences.setLoggedIn(true)
        return AuthResult.Success
    }

    suspend fun logout() {
        userPreferences.setLoggedIn(false)
    }

    suspend fun updateName(name: String) {
        userPreferences.setName(name.trim())
    }

    // Removes the account so a new one can be created on this phone
    suspend fun deleteAccount() {
        userPreferences.clearAll()
    }

    // Turns "password" + salt into a scrambled string. The same input always gives the same output,
    // but you can't turn the output back into the password.
    private fun hash(password: String, salt: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((salt + password).toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // Random value saved next to the hash, so two people with the same password get different hashes
    private fun newSalt(): String {
        val bytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
