package com.amimin.app.data.database

import java.util.Calendar
import java.util.TimeZone

/**
 * Cálculo puro de la próxima activación de una alarma.
 *
 * `repeatDays` usa la convención de la interfaz: 1 = lunes … 7 = domingo.
 * `Calendar.DAY_OF_WEEK` usa 1 = domingo … 7 = sábado, así que se traduce explícitamente.
 */
object AlarmTimeCalculator {

    fun nextTrigger(
        nowMillis: Long,
        hour: Int,
        minute: Int,
        repeatDays: List<Int>,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val safeHour = hour.coerceIn(0, 23)
        val safeMinute = minute.coerceIn(0, 59)

        val base = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, safeHour)
            set(Calendar.MINUTE, safeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val validDays = repeatDays.filter { it in 1..7 }.toSet()
        if (validDays.isEmpty()) {
            if (base.timeInMillis <= nowMillis) {
                base.add(Calendar.DAY_OF_YEAR, 1)
            }
            return base.timeInMillis
        }

        // 0 = hoy, 7 = el mismo día de la semana siguiente. Cubre cualquier combinación.
        for (offset in 0..7) {
            val candidate = (base.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, offset)
            }
            if (candidate.timeInMillis <= nowMillis) continue
            if (calendarDayToUiDay(candidate.get(Calendar.DAY_OF_WEEK)) in validDays) {
                return candidate.timeInMillis
            }
        }

        // Inalcanzable con datos válidos; se usa una semana de margen como red de seguridad.
        return (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }.timeInMillis
    }

    /** Traduce `Calendar.SUNDAY..SATURDAY` a la convención de la UI (lunes = 1). */
    fun calendarDayToUiDay(calendarDay: Int): Int = when (calendarDay) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        else -> 7 // Calendar.SUNDAY
    }
}
