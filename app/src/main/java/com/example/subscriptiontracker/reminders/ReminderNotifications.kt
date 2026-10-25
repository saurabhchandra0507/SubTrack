package com.example.subscriptiontracker.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.subscriptiontracker.MainActivity
import com.example.subscriptiontracker.R

// Everything about showing reminder notifications
object ReminderNotifications {

    private const val CHANNEL_ID = "renewal_reminders"
    private const val TEST_NOTIFICATION_ID = -1

    // Since Android 8 every notification needs a channel. Creating it twice is harmless.
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Renewal reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "A heads-up the day before a subscription renews"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    // On Android 13+ the user must allow notifications. Older versions allow them by default.
    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun showRenewal(context: Context, id: Long, title: String, text: String) {
        show(context, notificationId = id.toInt(), title = title, text = text)
    }

    fun showTest(context: Context) {
        show(
            context,
            notificationId = TEST_NOTIFICATION_ID,
            title = "Reminders are on ✅",
            text = "Nothing renews today or tomorrow. You'll get a note like this the day before each payment."
        )
    }

    private fun show(context: Context, notificationId: Int, title: String, text: String) {
        if (!hasPermission(context)) return

        // What happens when the notification is tapped: open the app
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFF7C5CFF.toInt()) // violet accent
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text)) // show the full text when expanded
            .setContentIntent(openApp)
            .setAutoCancel(true) // disappears after it's tapped
            .build()

        // Using the subscription id as the notification id means one notification per subscription
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission was taken away at the last moment; nothing to show
        }
    }
}
