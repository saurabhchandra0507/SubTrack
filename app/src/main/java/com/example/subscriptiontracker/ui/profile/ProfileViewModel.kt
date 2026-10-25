package com.example.subscriptiontracker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptiontracker.data.AppCurrency
import com.example.subscriptiontracker.data.AuthRepository
import com.example.subscriptiontracker.data.SettingsRepository
import com.example.subscriptiontracker.data.SubscriptionRepository
import com.example.subscriptiontracker.data.ThemeMode
import com.example.subscriptiontracker.reminders.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val subscriptionCount: Int = 0,
    val currency: AppCurrency = AppCurrency.INR,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val remindersEnabled: Boolean = false,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    // combine() can take up to 5 flows; any change rebuilds the state
    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.account,
        settingsRepository.currency,
        settingsRepository.themeMode,
        subscriptionRepository.subscriptions,
        settingsRepository.remindersEnabled,
    ) { account, currency, themeMode, subscriptions, remindersEnabled ->
        ProfileUiState(
            name = account?.name.orEmpty(),
            email = account?.email.orEmpty(),
            subscriptionCount = subscriptions.size,
            currency = currency,
            themeMode = themeMode,
            remindersEnabled = remindersEnabled,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun updateName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { authRepository.updateName(name) }
    }

    fun setCurrency(currency: AppCurrency) {
        viewModelScope.launch { settingsRepository.setCurrency(currency) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    // Save the choice, then start or stop the daily background check
    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setRemindersEnabled(enabled)
            if (enabled) reminderScheduler.scheduleDaily() else reminderScheduler.cancelDaily()
        }
    }

    fun sendTestReminder() = reminderScheduler.runTestNow()

    suspend fun logout() = authRepository.logout()

    // Deletes all subscriptions first, then the account itself
    suspend fun deleteAccount() {
        reminderScheduler.cancelDaily()
        subscriptionRepository.deleteAll()
        authRepository.deleteAccount()
    }
}
