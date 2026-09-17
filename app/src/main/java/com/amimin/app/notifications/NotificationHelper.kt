package com.amimin.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_ALARM = "amimin_alarm_channel"
    const val CHANNEL_ALARM_REMINDER = "amimin_alarm_reminder_channel"
    const val CHANNEL_EVENT = "amimin_event_channel"

    fun localeFor(language: String): Locale = when (language) {
        "es" -> Locale("es")
        "ja" -> Locale("ja")
        "ko" -> Locale("ko")
        "zh" -> Locale("zh")
        "ru" -> Locale("ru")
        "fr" -> Locale("fr")
        "de" -> Locale("de")
        else -> Locale("en")
    }

    fun localizedContext(context: Context, language: String): Context {
        val locale = localeFor(language)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        val alarm = NotificationChannel(
            CHANNEL_ALARM,
            "Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alarm notifications"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
            setBypassDnd(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }

        val reminder = NotificationChannel(
            CHANNEL_ALARM_REMINDER,
            "Alarm reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders before an alarm rings"
            enableVibration(true)
        }

        val event = NotificationChannel(
            CHANNEL_EVENT,
            "Calendar events",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Calendar event reminders"
        }

        manager.createNotificationChannel(alarm)
        manager.createNotificationChannel(reminder)
        manager.createNotificationChannel(event)
    }

    fun getText(context: Context, language: String, resId: Int, vararg args: Any): String {
        val localized = localizedContext(context, language)
        return localized.getString(resId, *args)
    }
}
