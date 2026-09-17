package com.amimin.app.data.database

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val alarmLabel = intent.getStringExtra("alarm_label") ?: "Anime Alarm"
        val alarmHour = intent.getIntExtra("alarm_hour", 0)
        val alarmMinute = intent.getIntExtra("alarm_minute", 0)
        val alarmRingtone = intent.getStringExtra("alarm_ringtone")
        val language = intent.getStringExtra("language") ?: "en"
        val repeatDays = intent.getIntArrayExtra("repeat_days")?.toList() ?: emptyList()

        // Start the foreground alarm service (plays sound, shows full-screen notification)
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", alarmLabel)
            putExtra("alarm_ringtone", alarmRingtone)
            putExtra("language", language)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Reschedule for repeating alarms
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (repeatDays.isNotEmpty()) {
                    val alarm = alarmRepository.getAlarmById(alarmId)
                    if (alarm != null) {
                        alarmScheduler.schedule(alarm, language)
                    } else {
                        alarmScheduler.schedule(
                            Alarm(
                                id = alarmId,
                                hour = alarmHour,
                                minute = alarmMinute,
                                label = alarmLabel,
                                repeatDays = repeatDays
                            ),
                            language
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
