package com.amimin.app.ui.screens.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amimin.app.data.database.AlarmScheduler
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.repository.AlarmRepository
import com.amimin.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    application: Application,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    val use24HourFormat: StateFlow<Boolean> = settingsRepository.use24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _showTimePicker = MutableStateFlow(false)
    val showTimePicker: StateFlow<Boolean> = _showTimePicker.asStateFlow()

    private val _showLabelDialog = MutableStateFlow(false)
    val showLabelDialog: StateFlow<Boolean> = _showLabelDialog.asStateFlow()

    private val _selectedAlarm = MutableStateFlow<Alarm?>(null)
    val selectedAlarm: StateFlow<Alarm?> = _selectedAlarm.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            alarmRepository.getAllAlarms().collect { alarmList ->
                _alarms.value = alarmList
            }
        }
    }

    fun showTimePicker() { _showTimePicker.value = true }
    fun hideTimePicker() { _showTimePicker.value = false }

    fun showLabelDialog(alarm: Alarm? = null) {
        _selectedAlarm.value = alarm
        _showLabelDialog.value = true
    }

    fun hideLabelDialog() {
        _showLabelDialog.value = false
        _selectedAlarm.value = null
    }

    fun addAlarm(
        hour: Int,
        minute: Int,
        label: String,
        repeatDays: List<Int>,
        sticker: String? = null,
        photoUri: String? = null,
        ringtoneUri: String? = null
    ) {
        viewModelScope.launch {
            val alarm = Alarm(
                hour = hour,
                minute = minute,
                label = label.ifBlank { "Anime Alarm" },
                repeatDays = repeatDays,
                sticker = sticker,
                photoUri = photoUri,
                ringtoneUri = ringtoneUri
            )
            val id = alarmRepository.insertAlarm(alarm)
            val language = settingsRepository.language.first()
            alarmScheduler.schedule(alarm.copy(id = id), language)
        }
    }

    fun updateAlarmDetails(alarm: Alarm, sticker: String?, photoUri: String?, ringtoneUri: String?) {
        viewModelScope.launch {
            val updated = alarm.copy(sticker = sticker, photoUri = photoUri, ringtoneUri = ringtoneUri)
            alarmRepository.updateAlarm(updated)
            val language = settingsRepository.language.first()
            if (updated.isEnabled) alarmScheduler.schedule(updated, language) else alarmScheduler.cancel(updated)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.updateAlarm(alarm)
            val language = settingsRepository.language.first()
            if (alarm.isEnabled) alarmScheduler.schedule(alarm, language) else alarmScheduler.cancel(alarm)
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            alarmRepository.updateAlarm(updated)
            val language = settingsRepository.language.first()
            if (updated.isEnabled) alarmScheduler.schedule(updated, language) else alarmScheduler.cancel(updated)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.deleteAlarm(alarm)
            alarmScheduler.cancel(alarm)
        }
    }
}
