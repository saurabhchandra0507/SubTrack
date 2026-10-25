package com.example.subscriptiontracker.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Creates one DataStore file called "user_prefs" for the whole app
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

// What the rest of the app is allowed to see about the user (no password!)
data class UserAccount(val name: String, val email: String)

// What we need to check a password at login
data class StoredCredentials(val email: String, val passwordHash: String, val salt: String)

// Reads and writes the saved account + login status. Only raw storage, no rules.
class UserPreferences(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD_HASH = stringPreferencesKey("password_hash")
        val SALT = stringPreferencesKey("salt")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val CURRENCY = stringPreferencesKey("currency")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
    }

    // A Flow sends a new value every time the data changes
    val account: Flow<UserAccount?> = context.dataStore.data.map { prefs ->
        val email = prefs[Keys.EMAIL] ?: return@map null
        UserAccount(name = prefs[Keys.NAME].orEmpty(), email = email)
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOGGED_IN] ?: false
    }

    // Saved as text like "USD". If it's missing or unknown, fall back to the default.
    val currency: Flow<AppCurrency> = context.dataStore.data.map { prefs ->
        AppCurrency.entries.find { it.name == prefs[Keys.CURRENCY] } ?: AppCurrency.INR
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.entries.find { it.name == prefs[Keys.THEME_MODE] } ?: ThemeMode.SYSTEM
    }

    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.REMINDERS_ENABLED] ?: false
    }

    // first() reads the current value once instead of listening forever
    suspend fun getCredentials(): StoredCredentials? {
        val prefs = context.dataStore.data.first()
        val email = prefs[Keys.EMAIL] ?: return null
        return StoredCredentials(
            email = email,
            passwordHash = prefs[Keys.PASSWORD_HASH].orEmpty(),
            salt = prefs[Keys.SALT].orEmpty()
        )
    }

    suspend fun saveAccount(name: String, email: String, passwordHash: String, salt: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = name
            prefs[Keys.EMAIL] = email
            prefs[Keys.PASSWORD_HASH] = passwordHash
            prefs[Keys.SALT] = salt
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.LOGGED_IN] = loggedIn }
    }

    suspend fun setName(name: String) {
        context.dataStore.edit { prefs -> prefs[Keys.NAME] = name }
    }

    suspend fun setCurrency(currency: AppCurrency) {
        context.dataStore.edit { prefs -> prefs[Keys.CURRENCY] = currency.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.REMINDERS_ENABLED] = enabled }
    }

    // Wipes everything: account, login status and settings
    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
