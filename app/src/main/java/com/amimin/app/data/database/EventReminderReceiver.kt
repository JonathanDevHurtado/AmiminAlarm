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

class EventReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra("event_id", 0L)
        val title = intent.getStringExtra("event_title") ?: ""
        val description = intent.getStringExtra("event_description") ?: ""
        val eventTime = intent.getLongExtra("event_time", 0L)
        val language = intent.getStringExtra("language") ?: "en"

        NotificationHelper.ensureChannels(context)
        val localized = NotificationHelper.localizedContext(context, language)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.toInt() + 40000,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val timeText = if (eventTime > 0) formatter.format(java.util.Date(eventTime)) else ""
        val text = if (description.isNotBlank()) description else timeText

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_EVENT)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify((eventId.toInt() + 60000), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
