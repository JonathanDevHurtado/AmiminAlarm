package com.amimin.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amimin.app.data.repository.SettingsRepository
import com.amimin.app.data.repository.SettingsRepository.ColorSource
import com.amimin.app.data.repository.SettingsRepository.LiveWallpaper
import com.amimin.app.ui.animations.LiveWallpaperStyle
import androidx.compose.ui.graphics.toArgb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CardColors(val alarm: List<Int>, val calendar: List<Int>)

@HiltViewModel
class HomeViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val username: StateFlow<String> = settingsRepository.username
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val use24HourFormat: StateFlow<Boolean> = settingsRepository.use24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val cardColors: StateFlow<CardColors> = combine(
        settingsRepository.colorSource,
        settingsRepository.themeName,
        settingsRepository.customPrimaryColor,
        settingsRepository.customSecondaryColor,
        settingsRepository.wallpaperPrimaryColor,
        settingsRepository.wallpaperSecondaryColor,
        settingsRepository.wallpaperAccentColor,
        settingsRepository.liveWallpaperEnabled,
        settingsRepository.liveWallpaperStyle
    ) { values ->
        val colorSource = values[0] as String
        val themeName = values[1] as String
        val customPrimary = values[2] as Int
        val customSecondary = values[3] as Int
        val wpPrimary = values[4] as Int
        val wpSecondary = values[5] as Int
        val wpAccent = values[6] as Int
        val liveEnabled = values[7] as Boolean
        val liveStyle = values[8] as String

        val base: List<Int> = when {
            liveEnabled && liveStyle != LiveWallpaper.NONE ->
                LiveWallpaperStyle.paletteFor(liveStyle).map { it.toArgb() }
            colorSource == ColorSource.WALLPAPER ->
                listOf(wpPrimary, wpSecondary, wpAccent)
            colorSource == ColorSource.CUSTOM ->
                listOf(customPrimary, customSecondary)
            else -> {
                val palette = com.amimin.app.ui.theme.AnimeThemePalettes.fromName(themeName)
                listOf(palette.primary.toArgb(), palette.secondary.toArgb(), palette.accent.toArgb())
            }
        }

        val alarm = if (base.size >= 2) listOf(base[0], base[1]) else listOf(base[0], base[0])
        val calendar = when {
            base.size >= 3 -> listOf(base[1], base[2])
            base.size == 2 -> listOf(base[1], base[0])
            else -> listOf(base[0], base[0])
        }
        CardColors(alarm = alarm, calendar = calendar)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CardColors(
        alarm = listOf(0xFFFF6B9D.toInt(), 0xFFFF8E53.toInt()),
        calendar = listOf(0xFF667EEA.toInt(), 0xFF764BA2.toInt())
    ))
}
