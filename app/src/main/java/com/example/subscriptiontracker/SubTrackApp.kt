package com.example.subscriptiontracker

import android.app.Application
import com.example.subscriptiontracker.data.AppContainer
import com.example.subscriptiontracker.reminders.ReminderNotifications

// Runs before any screen opens (registered in AndroidManifest.xml as android:name)
class SubTrackApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ReminderNotifications.createChannel(this)
    }
}
