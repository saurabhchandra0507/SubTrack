package com.example.subscriptiontracker.data

import kotlinx.coroutines.flow.Flow

// App preferences that aren't about logging in
class SettingsRepository(private val userPreferences: UserPreferences) {

    val currency: Flow<AppCurrency> = userPreferences.currency
    val themeMode: Flow<ThemeMode> = userPreferences.themeMode
    val remindersEnabled: Flow<Boolean> = userPreferences.remindersEnabled

    suspend fun setCurrency(currency: AppCurrency) = userPreferences.setCurrency(currency)

    suspend fun setThemeMode(mode: ThemeMode) = userPreferences.setThemeMode(mode)

    suspend fun setRemindersEnabled(enabled: Boolean) = userPreferences.setRemindersEnabled(enabled)
}
