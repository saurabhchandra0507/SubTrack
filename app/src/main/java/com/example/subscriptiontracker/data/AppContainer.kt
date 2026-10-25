package com.example.subscriptiontracker.data

import android.content.Context
import androidx.room.Room
import com.example.subscriptiontracker.reminders.ReminderScheduler

// Creates each repository once and shares it with the whole app
class AppContainer(context: Context) {
    private val userPreferences = UserPreferences(context)
    val authRepository = AuthRepository(userPreferences)
    val settingsRepository = SettingsRepository(userPreferences)

    // The database file "subtrack.db" is created the first time it's used
    private val database: AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "subtrack.db").build()
    val subscriptionRepository = SubscriptionRepository(database.subscriptionDao())

    val reminderScheduler = ReminderScheduler(context)
}
