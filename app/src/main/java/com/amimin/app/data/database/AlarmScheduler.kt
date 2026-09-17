package com.amimin.app.data.database

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent
import com.amimin.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private fun alarmManager(): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun setExact(timeInMillis: Long, pendingIntent: PendingIntent) {
        val manager = alarmManager()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (manager.canScheduleExactAlarms()) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            manager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }

    private fun nextTrigger(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }

    suspend fun schedule(alarm: Alarm, language: String) {
        if (!alarm.isEnabled) {
            cancel(alarm)
            return
        }

        val trigger = nextTrigger(alarm.hour, alarm.minute)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id)
            putExtra("alarm_label", alarm.label)
            putExtra("alarm_hour", alarm.hour)
            putExtra("alarm_minute", alarm.minute)
            putExtra("alarm_ringtone", alarm.ringtoneUri)
            putExtra("language", language)
            putExtra("repeat_days", alarm.repeatDays.toIntArray())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setExact(trigger, pendingIntent)

        scheduleReminders(alarm, trigger, language)
    }

    private suspend fun scheduleReminders(alarm: Alarm, triggerAt: Long, language: String) {
        val now = System.currentTimeMillis()
        val options = listOf(
            Triple(settingsRepository.reminder1h.first(), 60, 1),
            Triple(settingsRepository.reminder30m.first(), 30, 2),
            Triple(settingsRepository.reminder10m.first(), 10, 3)
        )
        options.forEach { (enabled, minutesBefore, slot) ->
            val intent = Intent(context, AlarmReminderReceiver::class.java).apply {
                putExtra("alarm_id", alarm.id)
                putExtra("alarm_label", alarm.label)
                putExtra("alarm_hour", alarm.hour)
                putExtra("alarm_minute", alarm.minute)
                putExtra("minutes_before", minutesBefore)
                putExtra("language", language)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (alarm.id.toInt() * 10) + slot,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val reminderTime = triggerAt - minutesBefore * 60_000L
            if (enabled && reminderTime > now) {
                setExact(reminderTime, pendingIntent)
            } else {
                alarmManager().cancel(pendingIntent)
            }
        }
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager().cancel(pendingIntent)

        listOf(1, 2, 3).forEach { slot ->
            val reminderIntent = Intent(context, AlarmReminderReceiver::class.java)
            val reminderPending = PendingIntent.getBroadcast(
                context,
                (alarm.id.toInt() * 10) + slot,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager().cancel(reminderPending)
        }
    }

    fun snooze(alarmId: Long, minutes: Int, label: String = "Anime Alarm") {
        val triggerAt = System.currentTimeMillis() + minutes * 60_000L
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
            putExtra("snooze", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId.toInt() + 900),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setExact(triggerAt, pendingIntent)
    }

    suspend fun scheduleEventReminder(event: CalendarEvent, language: String) {
        if (event.reminderMinutes <= 0) return
        val triggerAt = event.startTime - event.reminderMinutes * 60_000L
        if (triggerAt <= System.currentTimeMillis()) return

        val intent = Intent(context, EventReminderReceiver::class.java).apply {
            putExtra("event_id", event.id)
            putExtra("event_title", event.title)
            putExtra("event_description", event.description)
            putExtra("event_time", event.startTime)
            putExtra("language", language)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (event.id.toInt() + 50000),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setExact(triggerAt, pendingIntent)
    }

    fun cancelEventReminder(event: CalendarEvent) {
        val intent = Intent(context, EventReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (event.id.toInt() + 50000),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager().cancel(pendingIntent)
    }
}
