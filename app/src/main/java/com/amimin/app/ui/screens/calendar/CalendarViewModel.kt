package com.amimin.app.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amimin.app.data.database.AlarmScheduler
import com.amimin.app.data.model.CalendarEvent
import com.amimin.app.data.repository.CalendarRepository
import com.amimin.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    application: Application,
    private val calendarRepository: CalendarRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _selectedDateEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val selectedDateEvents: StateFlow<List<CalendarEvent>> = _selectedDateEvents.asStateFlow()

    private val _showEventDialog = MutableStateFlow(false)
    val showEventDialog: StateFlow<Boolean> = _showEventDialog.asStateFlow()

    private val _editingEvent = MutableStateFlow<CalendarEvent?>(null)
    val editingEvent: StateFlow<CalendarEvent?> = _editingEvent.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            val startOfMonth = _currentMonth.value.atDay(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = _currentMonth.value.atEndOfMonth()
                .atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            calendarRepository.getEventsInRange(startOfMonth, endOfMonth).collect { eventList ->
                _events.value = expandRecurrences(eventList, startOfMonth, endOfMonth)
            }
        }
    }

    private fun expandRecurrences(events: List<CalendarEvent>, rangeStart: Long, rangeEnd: Long): List<CalendarEvent> {
        val result = mutableListOf<CalendarEvent>()
        val zone = ZoneId.systemDefault()
        events.forEach { event ->
            if (event.recurrence == "none" || event.recurrence.isBlank()) {
                result.add(event)
                return@forEach
            }
            val originalStart = java.time.Instant.ofEpochMilli(event.startTime).atZone(zone).toLocalDateTime()
            val duration = event.endTime - event.startTime
            var occurrence = originalStart
            var guard = 0
            while (guard < 400) {
                val occMillis = occurrence.atZone(zone).toInstant().toEpochMilli()
                if (occMillis > rangeEnd) break
                if (occMillis >= rangeStart - duration) {
                    if (occMillis != event.startTime) {
                        result.add(event.copy(startTime = occMillis, endTime = occMillis + duration))
                    } else {
                        result.add(event)
                    }
                }
                occurrence = when (event.recurrence) {
                    "weekly" -> occurrence.plusWeeks(1)
                    "monthly" -> occurrence.plusMonths(1)
                    "yearly" -> occurrence.plusYears(1)
                    else -> return@forEach
                }
                guard++
            }
        }
        return result
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadEventsForDate(date)
    }

    private fun loadEventsForDate(date: LocalDate) {
        viewModelScope.launch {
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val dayEvents = calendarRepository.getEventsForDate(startOfDay, endOfDay)
            _selectedDateEvents.value = expandRecurrences(dayEvents, startOfDay, endOfDay)
        }
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadEvents()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadEvents()
    }

    fun showAddEvent() {
        _editingEvent.value = null
        _showEventDialog.value = true
    }

    fun showEditEvent(event: CalendarEvent) {
        _editingEvent.value = event
        _showEventDialog.value = true
    }

    fun hideEventDialog() {
        _showEventDialog.value = false
        _editingEvent.value = null
    }

    fun addEvent(
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        color: String,
        isAllDay: Boolean,
        reminderMinutes: Int,
        sticker: String? = null,
        photoUri: String? = null,
        recurrence: String = "none"
    ) {
        viewModelScope.launch {
            val event = CalendarEvent(
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                color = color,
                isAllDay = isAllDay,
                reminderMinutes = reminderMinutes,
                animeSticker = sticker,
                photoUri = photoUri,
                recurrence = recurrence
            )
            val id = calendarRepository.insertEvent(event)
            val language = settingsRepository.language.first()
            if (settingsRepository.eventNotifications.first()) {
                alarmScheduler.scheduleEventReminder(event.copy(id = id), language)
            }
            loadEvents()
            loadEventsForDate(_selectedDate.value)
        }
    }

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            calendarRepository.updateEvent(event)
            val language = settingsRepository.language.first()
            alarmScheduler.cancelEventReminder(event)
            if (settingsRepository.eventNotifications.first()) {
                alarmScheduler.scheduleEventReminder(event, language)
            }
            loadEvents()
            loadEventsForDate(_selectedDate.value)
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            calendarRepository.deleteEvent(event)
            alarmScheduler.cancelEventReminder(event)
            loadEvents()
            loadEventsForDate(_selectedDate.value)
        }
    }

    fun goToMonth(month: YearMonth) {
        _currentMonth.value = month
        loadEvents()
    }

    fun jumpMonths(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusMonths(delta.toLong())
        loadEvents()
    }

    fun jumpYears(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusYears(delta.toLong())
        loadEvents()
    }

    fun getDaysInMonth(): List<LocalDate> {
        val yearMonth = _currentMonth.value
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDay = yearMonth.atDay(1)
        val daysBefore = (firstDay.dayOfWeek.value - 1) % 7

        val days = mutableListOf<LocalDate>()
        for (i in 0 until daysBefore) {
            days.add(firstDay.minusDays((daysBefore - i).toLong()))
        }
        for (i in 1..daysInMonth) {
            days.add(yearMonth.atDay(i))
        }
        val remaining = 42 - days.size
        val nextMonth = yearMonth.plusMonths(1)
        for (i in 1..remaining) {
            days.add(nextMonth.atDay(i))
        }
        return days
    }

    fun getMonthTitle(): String {
        val month = _currentMonth.value.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return "$month ${_currentMonth.value.year}"
    }

    fun getWeekDays(): List<String> {
        val locale = Locale.getDefault()
        return listOf(
            java.time.DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.TUESDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.THURSDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.FRIDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.SATURDAY.getDisplayName(TextStyle.SHORT, locale),
            java.time.DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, locale)
        )
    }
}
