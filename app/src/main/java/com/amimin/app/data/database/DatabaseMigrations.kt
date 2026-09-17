package com.amimin.app.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones que PRESERVAN los datos del usuario al actualizar la app.
 * Nunca usar fallbackToDestructiveMigration: borraría alarmas y eventos.
 */
object DatabaseMigrations {

    /** v1 -> v2: stickers y fotos personalizables en alarmas y eventos. */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE alarms ADD COLUMN sticker TEXT")
            db.execSQL("ALTER TABLE alarms ADD COLUMN photoUri TEXT")
            db.execSQL("ALTER TABLE calendar_events ADD COLUMN photoUri TEXT")
        }
    }

    /** v2 -> v3: repetición de eventos (semanal / mensual / anual). */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE calendar_events ADD COLUMN recurrence TEXT NOT NULL DEFAULT 'none'")
        }
    }

    val ALL = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}
