package com.amimin.app.ui.screens.home

import java.time.LocalDateTime
import java.util.Locale

data class GreetingSchedule(
    val morningStart: Int,
    val afternoonStart: Int,
    val nightStart: Int
)

enum class GreetingPeriod { MORNING, AFTERNOON, NIGHT }

fun scheduleFor(locale: Locale): GreetingSchedule = when {
    locale.language == "es" && locale.country.equals("ES", ignoreCase = true) ->
        GreetingSchedule(morningStart = 6, afternoonStart = 14, nightStart = 21)
    locale.language == "es" ->
        GreetingSchedule(morningStart = 6, afternoonStart = 12, nightStart = 19)
    locale.language == "ja" ->
        GreetingSchedule(morningStart = 5, afternoonStart = 11, nightStart = 18)
    locale.language == "ko" ->
        GreetingSchedule(morningStart = 5, afternoonStart = 12, nightStart = 18)
    locale.language == "zh" ->
        GreetingSchedule(morningStart = 5, afternoonStart = 12, nightStart = 18)
    else ->
        GreetingSchedule(morningStart = 5, afternoonStart = 12, nightStart = 18)
}

fun periodFor(hour: Int, schedule: GreetingSchedule): GreetingPeriod = when {
    hour >= schedule.nightStart || hour < schedule.morningStart -> GreetingPeriod.NIGHT
    hour >= schedule.afternoonStart -> GreetingPeriod.AFTERNOON
    else -> GreetingPeriod.MORNING
}

fun getGreeting(
    locale: Locale = Locale.getDefault(),
    hour: Int = LocalDateTime.now().hour
): String {
    val period = periodFor(hour, scheduleFor(locale))
    return when (locale.language) {
        "es" -> when (period) {
            GreetingPeriod.MORNING -> "Buenos días"
            GreetingPeriod.AFTERNOON -> "Buenas tardes"
            GreetingPeriod.NIGHT -> "Buenas noches"
        }
        "ja" -> when (period) {
            GreetingPeriod.MORNING -> "おはようございます"
            GreetingPeriod.AFTERNOON -> "こんにちは"
            GreetingPeriod.NIGHT -> "こんばんは"
        }
        "ko" -> when (period) {
            GreetingPeriod.MORNING -> "좋은 아침이에요"
            GreetingPeriod.AFTERNOON -> "안녕하세요"
            GreetingPeriod.NIGHT -> "좋은 저녁이에요"
        }
        "zh" -> when (period) {
            GreetingPeriod.MORNING -> "早上好"
            GreetingPeriod.AFTERNOON -> "下午好"
            GreetingPeriod.NIGHT -> "晚上好"
        }
        "ru" -> when (period) {
            GreetingPeriod.MORNING -> "Доброе утро"
            GreetingPeriod.AFTERNOON -> "Добрый день"
            GreetingPeriod.NIGHT -> "Добрый вечер"
        }
        "fr" -> when (period) {
            GreetingPeriod.MORNING -> "Bonjour"
            GreetingPeriod.AFTERNOON -> "Bon après-midi"
            GreetingPeriod.NIGHT -> "Bonsoir"
        }
        "de" -> when (period) {
            GreetingPeriod.MORNING -> "Guten Morgen"
            GreetingPeriod.AFTERNOON -> "Guten Tag"
            GreetingPeriod.NIGHT -> "Guten Abend"
        }
        else -> when (period) {
            GreetingPeriod.MORNING -> "Good Morning"
            GreetingPeriod.AFTERNOON -> "Good Afternoon"
            GreetingPeriod.NIGHT -> "Good Evening"
        }
    }
}
