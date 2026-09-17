package com.amimin.app.data.database

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amimin.app.data.repository.AlarmRepository
import com.amimin.app.data.repository.CalendarRepository
import com.amimin.app.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var calendarRepository: CalendarRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            action != "android.intent.action.QUICKBOOT_POWERON"
        ) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val language = settingsRepository.language.first()

                val alarms = alarmRepository.getAllAlarmsOnce()
                alarms.filter { it.isEnabled }.forEach { alarm ->
                    alarmScheduler.schedule(alarm, language)
                }

                val now = System.currentTimeMillis()
                val events = calendarRepository.getAllEventsOnce()
                events.filter { it.startTime > now }.forEach { event ->
                    alarmScheduler.scheduleEventReminder(event, language)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
