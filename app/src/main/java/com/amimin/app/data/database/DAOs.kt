package com.amimin.app.data.database

import androidx.room.*
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    suspend fun getAllAlarmsOnce(): List<Alarm>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    fun getEnabledAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): Alarm?

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 AND (repeatDays LIKE '%' || :day || '%' OR repeatDays = '[]')")
    suspend fun getAlarmsForDay(day: Int): List<Alarm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Long, enabled: Boolean)
}

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events WHERE startTime BETWEEN :startDate AND :endDate ORDER BY startTime")
    fun getEventsInRange(startDate: Long, endDate: Long): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM calendar_events ORDER BY startTime")
    suspend fun getAllEventsOnce(): List<CalendarEvent>

    @Query("SELECT * FROM calendar_events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): CalendarEvent?

    @Query("SELECT * FROM calendar_events WHERE startTime BETWEEN :startDate AND :endDate")
    suspend fun getEventsForDate(startDate: Long, endDate: Long): List<CalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Update
    suspend fun updateEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)
}
