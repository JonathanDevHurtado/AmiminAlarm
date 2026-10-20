package com.amimin.app.di

import android.content.Context
import androidx.room.Room
import com.amimin.app.data.database.AlarmDao
import com.amimin.app.data.database.AmiminDatabase
import com.amimin.app.data.database.CalendarEventDao
import com.amimin.app.data.database.DatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AmiminDatabase {
        return Room.databaseBuilder(
            context,
            AmiminDatabase::class.java,
            "amimin_database"
        )
            .addMigrations(*DatabaseMigrations.ALL)
            .build()
    }

    @Provides
    fun provideAlarmDao(database: AmiminDatabase): AlarmDao {
        return database.alarmDao()
    }

    @Provides
    fun provideCalendarEventDao(database: AmiminDatabase): CalendarEventDao {
        return database.calendarEventDao()
    }
}
