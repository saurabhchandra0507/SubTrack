package com.example.subscriptiontracker.reminders

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

// Tells WorkManager when to run RenewalReminderWorker
class ReminderScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    // Run once a day, starting at the next 9:00 AM.
    // WorkManager remembers this even after the phone restarts.
    fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<RenewalReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNext9am().toMinutes(), TimeUnit.MINUTES)
            .build()

        // "Unique" = only one daily job ever exists. UPDATE replaces it if it's already there.
        workManager.enqueueUniquePeriodicWork(DAILY_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun cancelDaily() {
        workManager.cancelUniqueWork(DAILY_WORK_NAME)
    }

    // Runs the same check right now, so you don't have to wait until tomorrow to see it work
    fun runTestNow() {
        val request = OneTimeWorkRequestBuilder<RenewalReminderWorker>()
            .setInputData(workDataOf(RenewalReminderWorker.KEY_IS_TEST to true))
            .build()
        workManager.enqueue(request)
    }

    private fun delayUntilNext9am(): Duration {
        val now = LocalDateTime.now()
        var next = now.with(LocalTime.of(9, 0))
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next)
    }

    private companion object {
        const val DAILY_WORK_NAME = "daily_renewal_reminders"
    }
}
