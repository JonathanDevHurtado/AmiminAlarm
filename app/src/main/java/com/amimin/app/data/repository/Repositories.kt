package com.amimin.app.data.repository

import com.amimin.app.data.database.AlarmDao
import com.amimin.app.data.database.CalendarEventDao
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao
) {
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()

    suspend fun getAllAlarmsOnce(): List<Alarm> = alarmDao.getAllAlarmsOnce()

    suspend fun insertAlarms(alarms: List<Alarm>) {
        alarms.forEach { alarmDao.insertAlarm(it.copy(id = 0)) }
    }

    suspend fun deleteAllAlarms() = alarmDao.getAllAlarmsOnce().forEach { alarmDao.deleteAlarm(it) }

    fun getEnabledAlarms(): Flow<List<Alarm>> = alarmDao.getEnabledAlarms()

    suspend fun getAlarmById(id: Long): Alarm? = alarmDao.getAlarmById(id)

    suspend fun getAlarmsForDay(day: Int): List<Alarm> = alarmDao.getAlarmsForDay(day)

    suspend fun insertAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)

    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    suspend fun setAlarmEnabled(alarmId: Long, enabled: Boolean) =
        alarmDao.setAlarmEnabled(alarmId, enabled)
}

@Singleton
class CalendarRepository @Inject constructor(
    private val calendarEventDao: CalendarEventDao
) {
    fun getEventsInRange(startDate: Long, endDate: Long): Flow<List<CalendarEvent>> =
        calendarEventDao.getEventsInRange(startDate, endDate)

    suspend fun getAllEventsOnce(): List<CalendarEvent> = calendarEventDao.getAllEventsOnce()

    suspend fun insertEvents(events: List<CalendarEvent>) {
        events.forEach { calendarEventDao.insertEvent(it.copy(id = 0)) }
    }

    suspend fun deleteAllEvents() = calendarEventDao.getAllEventsOnce().forEach { calendarEventDao.deleteEvent(it) }

    suspend fun getEventById(id: Long): CalendarEvent? = calendarEventDao.getEventById(id)

    suspend fun getEventsForDate(startDate: Long, endDate: Long): List<CalendarEvent> =
        calendarEventDao.getEventsForDate(startDate, endDate)

    suspend fun insertEvent(event: CalendarEvent): Long = calendarEventDao.insertEvent(event)

    suspend fun updateEvent(event: CalendarEvent) = calendarEventDao.updateEvent(event)

    suspend fun deleteEvent(event: CalendarEvent) = calendarEventDao.deleteEvent(event)
}
