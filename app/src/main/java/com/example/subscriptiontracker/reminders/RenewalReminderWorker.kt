package com.example.subscriptiontracker.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.subscriptiontracker.SubTrackApp
import com.example.subscriptiontracker.data.nextPaymentFrom
import com.example.subscriptiontracker.ui.formatPrice
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

// The background job. WorkManager creates it and calls doWork(), even when the app is closed.
class RenewalReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as SubTrackApp).container

        // Do nothing if the user turned reminders off or isn't logged in
        val enabled = container.settingsRepository.remindersEnabled.first()
        val loggedIn = container.authRepository.isLoggedIn.first()
        if (!enabled || !loggedIn) return Result.success()

        val currency = container.settingsRepository.currency.first()
        val today = LocalDate.now()
        var sentAny = false

        container.subscriptionRepository.subscriptions.first().forEach { sub ->
            val daysLeft = ChronoUnit.DAYS.between(today, sub.nextPaymentFrom(today))
            val whenText = when (daysLeft) {
                0L -> "today"
                1L -> "tomorrow"
                else -> return@forEach // not due soon, skip this one
            }
            ReminderNotifications.showRenewal(
                applicationContext,
                id = sub.id,
                title = "${sub.name} renews $whenText",
                text = "${formatPrice(sub.price, currency)} will be charged $whenText. Still using it?"
            )
            sentAny = true
        }

        // The "Send test reminder" button asks for a confirmation even when nothing is due
        if (!sentAny && inputData.getBoolean(KEY_IS_TEST, false)) {
            ReminderNotifications.showTest(applicationContext)
        }

        return Result.success()
    }

    companion object {
        const val KEY_IS_TEST = "is_test"
    }
}
