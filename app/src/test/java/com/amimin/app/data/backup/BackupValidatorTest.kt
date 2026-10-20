package com.amimin.app.data.backup

import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BackupValidatorTest {

    private fun validBackup() = AmiminBackup(
        alarms = listOf(
            Alarm(
                id = 3,
                hour = 7,
                minute = 30,
                label = "Despertar",
                repeatDays = listOf(1, 3, 5),
                volume = 80
            )
        ),
        events = listOf(
            CalendarEvent(
                id = 9,
                title = "Examen",
                startTime = 1_800_000_000_000,
                endTime = 1_800_003_600_000,
                reminderMinutes = 30
            )
        )
    )

    @Test
    fun `valid backup passes`() {
        assertNull(BackupValidator.validate(validBackup()))
    }

    @Test
    fun `wrong format is rejected`() {
        assertNotNull(BackupValidator.validate(validBackup().copy(format = "otra-app")))
    }

    @Test
    fun `unsupported version is rejected`() {
        assertNotNull(BackupValidator.validate(validBackup().copy(version = 99)))
    }

    @Test
    fun `invalid alarm hour is rejected`() {
        val backup = validBackup().copy(alarms = listOf(Alarm(hour = 25, minute = 0)))
        assertNotNull(BackupValidator.validate(backup))
    }

    @Test
    fun `invalid repeat day is rejected`() {
        val backup = validBackup().copy(
            alarms = listOf(Alarm(hour = 8, minute = 0, repeatDays = listOf(0, 8)))
        )
        assertNotNull(BackupValidator.validate(backup))
    }

    @Test
    fun `invalid alarm volume is rejected`() {
        val backup = validBackup().copy(
            alarms = listOf(Alarm(hour = 8, minute = 0, volume = 101))
        )
        assertNotNull(BackupValidator.validate(backup))
    }

    @Test
    fun `event ending before it starts is rejected`() {
        val backup = validBackup().copy(
            events = listOf(
                CalendarEvent(
                    title = "Inválido",
                    startTime = 2_000,
                    endTime = 1_000
                )
            )
        )
        assertNotNull(BackupValidator.validate(backup))
    }

    @Test
    fun `blank event title is rejected`() {
        val backup = validBackup().copy(
            events = listOf(
                CalendarEvent(
                    title = " ",
                    startTime = 1_000,
                    endTime = 2_000
                )
            )
        )
        assertNotNull(BackupValidator.validate(backup))
    }

    @Test
    fun `too many alarms are rejected`() {
        val alarms = List(BackupValidator.MAX_ALARMS + 1) {
            Alarm(hour = 8, minute = 0)
        }
        assertNotNull(BackupValidator.validate(validBackup().copy(alarms = alarms)))
    }
}
