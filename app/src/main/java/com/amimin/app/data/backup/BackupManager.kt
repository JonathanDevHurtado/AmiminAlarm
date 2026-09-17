package com.amimin.app.data.backup

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.amimin.app.data.model.Alarm
import com.amimin.app.data.model.CalendarEvent
import com.amimin.app.data.repository.AlarmRepository
import com.amimin.app.data.repository.CalendarRepository
import com.amimin.app.data.repository.SettingsRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class SettingsBackup(
    val themeName: String = "sakura",
    val isDarkMode: Boolean = false,
    val animeStyle: String = "cute",
    val particlesEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val showAnimeStickers: Boolean = true,
    val calendarView: String = "month",
    val language: String = "en",
    val customPrimaryColor: Int = 0xFFFFB7C5.toInt(),
    val customSecondaryColor: Int = 0xFF9C27B0.toInt(),
    val colorSource: String = "theme",
    val liveWallpaperEnabled: Boolean = false,
    val liveWallpaperStyle: String = "none",
    val username: String = "",
    val use24HourFormat: Boolean = true
)

data class AmiminBackup(
    val format: String = "amimin-backup",
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0",
    val alarms: List<Alarm> = emptyList(),
    val events: List<CalendarEvent> = emptyList(),
    val settings: SettingsBackup = SettingsBackup(),
    val wallpaperBase64: String? = null
)

data class BackupResult(val success: Boolean, val message: String)

@Singleton
class BackupManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository,
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun createBackup(): AmiminBackup = withContext(Dispatchers.IO) {
        val settings = SettingsBackup(
            themeName = settingsRepository.themeName.first(),
            isDarkMode = settingsRepository.isDarkMode.first(),
            animeStyle = settingsRepository.animeStyle.first(),
            particlesEnabled = settingsRepository.particlesEnabled.first(),
            animationsEnabled = settingsRepository.animationsEnabled.first(),
            showAnimeStickers = settingsRepository.showAnimeStickers.first(),
            calendarView = settingsRepository.calendarView.first(),
            language = settingsRepository.language.first(),
            customPrimaryColor = settingsRepository.customPrimaryColor.first(),
            customSecondaryColor = settingsRepository.customSecondaryColor.first(),
            colorSource = settingsRepository.colorSource.first(),
            liveWallpaperEnabled = settingsRepository.liveWallpaperEnabled.first(),
            liveWallpaperStyle = settingsRepository.liveWallpaperStyle.first(),
            username = settingsRepository.username.first(),
            use24HourFormat = settingsRepository.use24HourFormat.first()
        )

        val wallpaperBase64 = try {
            val bitmap = settingsRepository.getWallpaperBitmap()
            if (bitmap != null) {
                val stream = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, stream)
                Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            null
        }

        AmiminBackup(
            alarms = alarmRepository.getAllAlarmsOnce(),
            events = calendarRepository.getAllEventsOnce(),
            settings = settings,
            wallpaperBase64 = wallpaperBase64
        )
    }

    fun backupToJson(backup: AmiminBackup): String = gson.toJson(backup)

    fun suggestedFileName(): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return "amiminalarm-copia-de-seguridad-$date.json"
    }

    suspend fun writeBackupToUri(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            val backup = createBackup()
            val json = backupToJson(backup)
            context.contentResolver.openOutputStream(uri, "wt")?.use { out ->
                out.write(json.toByteArray(Charsets.UTF_8))
                out.flush()
            } ?: return@withContext BackupResult(false, "No se pudo abrir el archivo")
            BackupResult(true, "Copia guardada: ${backup.alarms.size} alarmas, ${backup.events.size} eventos")
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult(false, "Error al guardar: ${e.message}")
        }
    }

    suspend fun restoreFromUri(uri: Uri, replaceAll: Boolean = true): BackupResult = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            } ?: return@withContext BackupResult(false, "No se pudo leer el archivo")

            val backup = gson.fromJson(json, AmiminBackup::class.java)
                ?: return@withContext BackupResult(false, "Archivo inválido")

            if (backup.format != "amimin-backup") {
                return@withContext BackupResult(false, "El archivo no es una copia de Amimin")
            }

            if (replaceAll) {
                alarmRepository.deleteAllAlarms()
                calendarRepository.deleteAllEvents()
            }
            alarmRepository.insertAlarms(backup.alarms)
            calendarRepository.insertEvents(backup.events)

            backup.wallpaperBase64?.let { encoded ->
                try {
                    val bytes = Base64.decode(encoded, Base64.NO_WRAP)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        val file = File(context.filesDir, "amimin_wallpaper_restored.jpg")
                        java.io.FileOutputStream(file).use { out ->
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                        }
                        settingsRepository.setRestoredWallpaper(file.name)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            applySettings(backup.settings)

            BackupResult(true, "Restaurado: ${backup.alarms.size} alarmas, ${backup.events.size} eventos")
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult(false, "Error al restaurar: ${e.message}")
        }
    }

    private suspend fun applySettings(s: SettingsBackup) {
        settingsRepository.setThemeName(s.themeName)
        settingsRepository.setDarkMode(s.isDarkMode)
        settingsRepository.setAnimeStyle(s.animeStyle)
        settingsRepository.setParticlesEnabled(s.particlesEnabled)
        settingsRepository.setAnimationsEnabled(s.animationsEnabled)
        settingsRepository.setShowAnimeStickers(s.showAnimeStickers)
        settingsRepository.setCalendarView(s.calendarView)
        settingsRepository.setLanguage(s.language)
        settingsRepository.setCustomPrimaryColor(s.customPrimaryColor)
        settingsRepository.setCustomSecondaryColor(s.customSecondaryColor)
        settingsRepository.setColorSource(s.colorSource)
        settingsRepository.setLiveWallpaperEnabled(s.liveWallpaperEnabled)
        settingsRepository.setLiveWallpaperStyle(s.liveWallpaperStyle)
        settingsRepository.setUsername(s.username)
        settingsRepository.setUse24HourFormat(s.use24HourFormat)
    }
}
