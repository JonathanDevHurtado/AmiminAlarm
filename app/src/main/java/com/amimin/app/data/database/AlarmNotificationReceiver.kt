package com.amimin.app.data.database

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val label = intent.getStringExtra("alarm_label") ?: "Anime Alarm"
        val ringtone = intent.getStringExtra("alarm_ringtone")
        val vibrate = intent.getBooleanExtra("alarm_vibrate", true)
        val volume = intent.getIntExtra("alarm_volume", 80)
        val language = intent.getStringExtra("language") ?: "en"

        when (intent.action) {
            "DISMISS_ALARM" -> {
                AlarmService.stop(context)
                cancelNotifications(context)
            }
            "SNOOZE_ALARM" -> {
                AlarmService.stop(context)
                cancelNotifications(context)

                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("alarm_id", alarmId)
                    putExtra("alarm_label", label)
                    putExtra("alarm_ringtone", ringtone)
                    putExtra("alarm_vibrate", vibrate)
                    putExtra("alarm_volume", volume)
                    putExtra("language", language)
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
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    Log.w(TAG, "Sin permiso de alarmas exactas; se usa alarma inexacta", e)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            }
        }
    }

    private fun cancelNotifications(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AlarmService.NOTIFICATION_ID)
    }

    private companion object {
        const val TAG = "AlarmNotification"
    }
}
