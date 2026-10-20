package com.amimin.app.data.database

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amimin.app.MainActivity
import com.amimin.app.R
import com.amimin.app.notifications.NotificationHelper

class AlarmReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val alarmLabel = intent.getStringExtra("alarm_label") ?: "Anime Alarm"
        val alarmHour = intent.getIntExtra("alarm_hour", 0)
        val alarmMinute = intent.getIntExtra("alarm_minute", 0)
        val minutesBefore = intent.getIntExtra("minutes_before", 10)
        val language = intent.getStringExtra("language") ?: "en"

        NotificationHelper.ensureChannels(context)
        val localized = NotificationHelper.localizedContext(context, language)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt() + 30000,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = String.format("%02d:%02d", alarmHour, alarmMinute)
        val title = localized.getString(R.string.reminder_title, minutesBefore)
        val text = localized.getString(R.string.reminder_text, alarmLabel, timeText)

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ALARM_REMINDER)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify((alarmId.toInt() * 10) + minutesBefore, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
