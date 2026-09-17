package com.amimin.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Anime Alarm",
    val isEnabled: Boolean = true,
    val repeatDays: List<Int> = emptyList(), // 1=Mon, 7=Sun
    val ringtoneUri: String? = null,
    val vibrate: Boolean = true,
    val volume: Int = 80,
    val animeTheme: String = "sakura",
    val sticker: String? = null,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val color: String = "#FFB7C5",
    val isAllDay: Boolean = false,
    val reminderMinutes: Int = 30,
    val animeSticker: String? = null,
    val photoUri: String? = null,
    val recurrence: String = "none",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "alarms_disabled")
data class AlarmDisabled(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long,
    val disabledAt: Long = System.currentTimeMillis()
)
