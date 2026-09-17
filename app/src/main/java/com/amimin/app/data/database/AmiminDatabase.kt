package com.amimin.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent

@Database(
    entities = [Alarm::class, CalendarEvent::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AmiminDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun calendarEventDao(): CalendarEventDao
}
