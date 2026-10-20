package com.amimin.app.data.database

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class AlarmTimeCalculatorTest {

    private val utc: TimeZone = TimeZone.getTimeZone("UTC")

    private fun at(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): Long = Calendar.getInstance(utc).apply {
        clear()
        set(year, month - 1, day, hour, minute, 0)
    }.timeInMillis

    private fun next(
        now: Long,
        hour: Int,
        minute: Int,
        days: List<Int>
    ): Long = AlarmTimeCalculator.nextTrigger(now, hour, minute, days, utc)

    @Test
    fun `one shot later today stays today`() {
        val now = at(2026, 10, 19, 10, 0)
        assertEquals(at(2026, 10, 19, 11, 30), next(now, 11, 30, emptyList()))
    }

    @Test
    fun `one shot in the past moves to tomorrow`() {
        val now = at(2026, 10, 19, 10, 0)
        assertEquals(at(2026, 10, 20, 9, 0), next(now, 9, 0, emptyList()))
    }

    @Test
    fun `monday alarm scheduled from tuesday goes to next monday`() {
        val now = at(2026, 10, 20, 10, 0)
        assertEquals(at(2026, 10, 26, 9, 0), next(now, 9, 0, listOf(1)))
    }

    @Test
    fun `selected day later today is respected`() {
        val now = at(2026, 10, 20, 10, 0)
        assertEquals(at(2026, 10, 20, 11, 0), next(now, 11, 0, listOf(2)))
    }

    @Test
    fun `selected day already passed moves one week`() {
        val now = at(2026, 10, 20, 10, 0)
        assertEquals(at(2026, 10, 27, 9, 0), next(now, 9, 0, listOf(2)))
    }

    @Test
    fun `multiple selected days picks the nearest one`() {
        val now = at(2026, 10, 20, 10, 0)
        assertEquals(at(2026, 10, 22, 9, 0), next(now, 9, 0, listOf(1, 4)))
    }

    @Test
    fun `sunday uses the ui convention seven`() {
        val now = at(2026, 10, 24, 10, 0)
        assertEquals(at(2026, 10, 25, 9, 0), next(now, 9, 0, listOf(7)))
    }

    @Test
    fun `exact current time is not scheduled twice`() {
        val now = at(2026, 10, 19, 10, 0)
        assertEquals(at(2026, 10, 26, 10, 0), next(now, 10, 0, listOf(1)))
    }

    @Test
    fun `invalid repeat days fall back to daily`() {
        val now = at(2026, 10, 19, 10, 0)
        assertEquals(at(2026, 10, 20, 9, 0), next(now, 9, 0, listOf(0, 8)))
    }

    @Test
    fun `calendar day is translated to ui day`() {
        assertEquals(1, AlarmTimeCalculator.calendarDayToUiDay(Calendar.MONDAY))
        assertEquals(6, AlarmTimeCalculator.calendarDayToUiDay(Calendar.SATURDAY))
        assertEquals(7, AlarmTimeCalculator.calendarDayToUiDay(Calendar.SUNDAY))
    }
}
