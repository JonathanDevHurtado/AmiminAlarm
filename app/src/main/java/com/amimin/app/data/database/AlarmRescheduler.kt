package com.amimin.app.data.database

import com.amimin.app.data.repository.AlarmRepository
import com.amimin.app.data.repository.CalendarRepository
import com.amimin.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Recrea todos los PendingIntent de alarmas y recordatorios.
 *
 * AlarmManager pierde sus alarmas al reiniciar el dispositivo, al actualizar la app,
 * al cambiar la hora/zona horaria y al restaurar una copia de seguridad. Esta clase
 * centraliza esa reconstrucción para que todos esos casos usen la misma lógica.
 */
@Singleton
class AlarmRescheduler @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend fun rescheduleAll() {
        val language = settingsRepository.language.first()

        alarmRepository.getAllAlarmsOnce().forEach { alarm ->
            if (alarm.isEnabled) {
                alarmScheduler.schedule(alarm, language)
            } else {
                alarmScheduler.cancel(alarm)
            }
        }

        val now = System.currentTimeMillis()
        calendarRepository.getAllEventsOnce().forEach { event ->
            if (event.startTime > now && event.reminderMinutes > 0) {
                alarmScheduler.scheduleEventReminder(event, language)
            } else {
                alarmScheduler.cancelEventReminder(event)
            }
        }
    }
}
