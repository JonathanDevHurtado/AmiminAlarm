package com.amimin.app.data.database

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)

        when (intent.action) {
            "DISMISS_ALARM" -> {
                AlarmService.stop(context)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(alarmId.toInt())
                notificationManager.cancel(AlarmService.NOTIFICATION_ID)
            }
            "SNOOZE_ALARM" -> {
                AlarmService.stop(context)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(alarmId.toInt())
                notificationManager.cancel(AlarmService.NOTIFICATION_ID)

                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("alarm_id", alarmId)
                    putExtra("alarm_label", "Snoozed Alarm")
                    putExtra("snooze", true)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId.toInt() + 900,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val triggerTime = System.currentTimeMillis() + 5 * 60 * 1000L
                try {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } catch (e: SecurityException) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            }
        }
    }
}
