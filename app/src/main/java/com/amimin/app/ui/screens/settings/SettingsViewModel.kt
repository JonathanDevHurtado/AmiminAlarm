package com.amimin.app.ui.screens.settings

import android.app.Application
import android.app.backup.BackupManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amimin.app.MainActivity
import com.amimin.app.data.backup.BackupManager as BackupHelper
import com.amimin.app.data.repository.SettingsRepository
import com.amimin.app.data.repository.SettingsRepository.ColorSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsRepository: SettingsRepository,
    private val backupHelper: BackupHelper
) : AndroidViewModel(application) {

    val themeName = settingsRepository.themeName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "sakura")
    val isDarkMode = settingsRepository.isDarkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val animeStyle = settingsRepository.animeStyle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "cute")
    val particlesEnabled = settingsRepository.particlesEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val animationsEnabled = settingsRepository.animationsEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val showAnimeStickers = settingsRepository.showAnimeStickers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val calendarView = settingsRepository.calendarView.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "month")
    val language = settingsRepository.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
    val wallpaperHasImage = settingsRepository.wallpaperHasImage.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val wallpaperPrimaryColor = settingsRepository.wallpaperPrimaryColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFFFFB7C5.toInt())
    val wallpaperSecondaryColor = settingsRepository.wallpaperSecondaryColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF9C27B0.toInt())
    val wallpaperAccentColor = settingsRepository.wallpaperAccentColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF00BCD4.toInt())
    val colorSource = settingsRepository.colorSource.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ColorSource.THEME)
    val useWallpaperColors = settingsRepository.useWallpaperColors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val liveWallpaperEnabled = settingsRepository.liveWallpaperEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val liveWallpaperStyle = settingsRepository.liveWallpaperStyle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsRepository.LiveWallpaper.NONE)
    val customPrimaryColor = settingsRepository.customPrimaryColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFFFFB7C5.toInt())
    val customSecondaryColor = settingsRepository.customSecondaryColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF9C27B0.toInt())
    val username = settingsRepository.username.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val use24HourFormat = settingsRepository.use24HourFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val liveWallpaperVideoUri = settingsRepository.liveWallpaperVideoUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val liveWallpaperMuted = settingsRepository.liveWallpaperMuted.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val reminder1h = settingsRepository.reminder1h.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val reminder30m = settingsRepository.reminder30m.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val reminder10m = settingsRepository.reminder10m.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val eventNotifications = settingsRepository.eventNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val alarmBackgroundUri = settingsRepository.alarmBackgroundUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val alarmBackgroundType = settingsRepository.alarmBackgroundType.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()
    private val _showColorPickerDialog = MutableStateFlow(false)
    val showColorPickerDialog: StateFlow<Boolean> = _showColorPickerDialog.asStateFlow()
    private val _wallpaperBitmap = MutableStateFlow<Bitmap?>(null)
    val wallpaperBitmap: StateFlow<Bitmap?> = _wallpaperBitmap.asStateFlow()

    init {
        loadWallpaperBitmap()
        observeWallpaperChanges()
    }

    private fun loadWallpaperBitmap() {
        viewModelScope.launch {
            _wallpaperBitmap.value = withContext(Dispatchers.IO) { settingsRepository.getWallpaperBitmap() }
        }
    }

    private fun observeWallpaperChanges() {
        viewModelScope.launch {
            settingsRepository.wallpaperHasImage.collect { hasImage ->
                _wallpaperBitmap.value = withContext(Dispatchers.IO) {
                    if (hasImage) settingsRepository.getWallpaperBitmap() else null
                }
            }
        }
    }

    fun setTheme(name: String) {
        viewModelScope.launch {
            settingsRepository.setThemeName(name)
            settingsRepository.setColorSource(ColorSource.THEME)
        }
    }

    fun setDarkMode(enabled: Boolean) { viewModelScope.launch { settingsRepository.setDarkMode(enabled) } }
    fun setAnimeStyle(style: String) { viewModelScope.launch { settingsRepository.setAnimeStyle(style) } }
    fun setParticlesEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepository.setParticlesEnabled(enabled) } }
    fun setAnimationsEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepository.setAnimationsEnabled(enabled) } }
    fun setShowAnimeStickers(enabled: Boolean) { viewModelScope.launch { settingsRepository.setShowAnimeStickers(enabled) } }
    fun setCalendarView(view: String) { viewModelScope.launch { settingsRepository.setCalendarView(view) } }

    fun setUseWallpaperColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseWallpaperColors(enabled)
        }
    }

    fun selectCustomColors() {
        viewModelScope.launch { settingsRepository.setColorSource(ColorSource.CUSTOM) }
    }

    fun setCustomPrimaryColor(color: Int) {
        viewModelScope.launch {
            settingsRepository.setCustomPrimaryColor(color)
            settingsRepository.setColorSource(ColorSource.CUSTOM)
        }
    }

    fun setCustomSecondaryColor(color: Int) {
        viewModelScope.launch {
            settingsRepository.setCustomSecondaryColor(color)
            settingsRepository.setColorSource(ColorSource.CUSTOM)
        }
    }

    fun setLiveWallpaperEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setLiveWallpaperEnabled(enabled) }
    }

    fun setLiveWallpaperStyle(style: String) {
        viewModelScope.launch { settingsRepository.setLiveWallpaperStyle(style) }
    }

    fun setLiveWallpaperMuted(muted: Boolean) {
        viewModelScope.launch { settingsRepository.setLiveWallpaperMuted(muted) }
    }

    fun setLiveWallpaperVideo(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                settingsRepository.setLiveWallpaperVideoUri(uri.toString())
                val color = com.amimin.app.ui.animations.extractColorFromVideo(getApplication(), uri.toString())
                settingsRepository.setLiveWallpaperVideoColor(color)
            }
        }
    }

    fun setReminder1h(enabled: Boolean) { viewModelScope.launch { settingsRepository.setReminder1h(enabled) } }
    fun setReminder30m(enabled: Boolean) { viewModelScope.launch { settingsRepository.setReminder30m(enabled) } }
    fun setReminder10m(enabled: Boolean) { viewModelScope.launch { settingsRepository.setReminder10m(enabled) } }
    fun setEventNotifications(enabled: Boolean) { viewModelScope.launch { settingsRepository.setEventNotifications(enabled) } }

    fun setAlarmBackground(type: String, uri: String) {
        viewModelScope.launch { settingsRepository.setAlarmBackground(type, uri) }
    }

    fun setUsername(name: String) {
        viewModelScope.launch { settingsRepository.setUsername(name) }
    }

    fun setUse24HourFormat(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setUse24HourFormat(enabled) }
    }

    fun showThemeDialog() { _showThemeDialog.value = true }
    fun hideThemeDialog() { _showThemeDialog.value = false }
    fun showColorPickerDialog() { _showColorPickerDialog.value = true }
    fun hideColorPickerDialog() { _showColorPickerDialog.value = false }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(lang)
            withContext(Dispatchers.Main) { MainActivity.changeLanguage(getApplication(), lang) }
        }
    }

    fun setWallpaperFromUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val success = settingsRepository.saveWallpaper(uri)
                    if (success) {
                        val bitmap = settingsRepository.getWallpaperBitmap()
                        if (bitmap != null) {
                            val palette = androidx.palette.graphics.Palette.from(bitmap).generate()
                            val primary = palette.getVibrantColor(0xFFFFB7C5.toInt())
                            val secondary = palette.getLightVibrantColor(0xFFCE93D8.toInt())
                            val accent = palette.getDarkVibrantColor(0xFF00BCD4.toInt())
                            settingsRepository.setWallpaperColors(primary, secondary, accent)
                            settingsRepository.setUseWallpaperColors(true)
                            withContext(Dispatchers.Main) { _wallpaperBitmap.value = bitmap }
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    fun clearWallpaper() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { settingsRepository.clearWallpaper() }
            withContext(Dispatchers.Main) { _wallpaperBitmap.value = null }
        }
    }

    fun getAvailableThemes() = listOf(
        "sakura" to "Sakura Pink", "midnight" to "Midnight Purple", "cherry" to "Cherry Red",
        "ocean" to "Ocean Blue", "neon" to "Neon Glow", "pastel" to "Pastel Dreams"
    )

    fun getLanguageDisplayName(code: String) = when (code) {
        "en" -> "English"; "es" -> "Español"; "ja" -> "日本語"; "ko" -> "한국어"
        "zh" -> "中文"; "ru" -> "Русский"; "fr" -> "Français"; "de" -> "Deutsch"
        else -> code
    }

    // ---- Backup / Restore ----

    private val _backupMessage = MutableStateFlow<String?>(null)
    val backupMessage: StateFlow<String?> = _backupMessage.asStateFlow()

    fun clearBackupMessage() { _backupMessage.value = null }

    fun suggestedBackupName(): String = backupHelper.suggestedFileName()

    fun backupToUri(uri: Uri) {
        viewModelScope.launch {
            val result = backupHelper.writeBackupToUri(uri)
            _backupMessage.value = result.message
        }
    }

    fun restoreFromUri(uri: Uri) {
        viewModelScope.launch {
            val result = backupHelper.restoreFromUri(uri)
            _backupMessage.value = result.message
            if (result.success) loadWallpaperBitmap()
        }
    }

    fun requestGoogleBackup() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val bm = BackupManager(getApplication())
                    bm.dataChanged()
                    _backupMessage.value = "Copia en la nube solicitada. Se sincronizará con tu cuenta de Google."
                } catch (e: Exception) {
                    _backupMessage.value = "Error: ${e.message}"
                }
            }
        }
    }

    fun shareBackupToCloud(onReady: (Intent) -> Unit) {
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) {
                try {
                    val backup = backupHelper.createBackup()
                    val json = backupHelper.backupToJson(backup)
                    val dir = File(getApplication<Application>().cacheDir, "backup")
                    if (!dir.exists()) dir.mkdirs()
                    val f = File(dir, backupHelper.suggestedFileName())
                    f.writeText(json)
                    f
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            if (file != null) {
                try {
                    val authority = "${getApplication<Application>().packageName}.fileprovider"
                    val uri = FileProvider.getUriForFile(getApplication(), authority, file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_SUBJECT, "AmiminAlarm - Copia de seguridad")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    withContext(Dispatchers.Main) {
                        onReady(Intent.createChooser(intent, "Guardar copia en..."))
                    }
                } catch (e: Exception) {
                    _backupMessage.value = "Error: ${e.message}"
                }
            } else {
                _backupMessage.value = "Error al crear la copia"
            }
        }
    }
}
