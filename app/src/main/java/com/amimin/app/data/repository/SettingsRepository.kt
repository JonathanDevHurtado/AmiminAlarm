package com.amimin.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "amimin_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    object ColorSource {
        const val THEME = "theme"
        const val CUSTOM = "custom"
        const val WALLPAPER = "wallpaper"
    }

    object LiveWallpaper {
        const val NONE = "none"
        const val GRADIENT = "gradient"
        const val SAKURA = "sakura"
        const val STARS = "stars"
        const val AURORA = "aurora"
        const val VIDEO = "video"
    }

    private object PreferencesKeys {
        val THEME_NAME = stringPreferencesKey("theme_name")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val ANIME_STYLE = stringPreferencesKey("anime_style")
        val PARTICLES_ENABLED = booleanPreferencesKey("particles_enabled")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val CUSTOM_PRIMARY_COLOR = intPreferencesKey("custom_primary_color")
        val CUSTOM_SECONDARY_COLOR = intPreferencesKey("custom_secondary_color")
        val CALENDAR_VIEW = stringPreferencesKey("calendar_view")
        val SHOW_ANIME_STICKERS = booleanPreferencesKey("show_anime_stickers")
        val LANGUAGE = stringPreferencesKey("language")
        val WALLPAPER_FILENAME = stringPreferencesKey("wallpaper_filename")
        val WALLPAPER_PRIMARY_COLOR = intPreferencesKey("wallpaper_primary_color")
        val WALLPAPER_SECONDARY_COLOR = intPreferencesKey("wallpaper_secondary_color")
        val WALLPAPER_ACCENT_COLOR = intPreferencesKey("wallpaper_accent_color")
        val COLOR_SOURCE = stringPreferencesKey("color_source")
        val LIVE_WALLPAPER_ENABLED = booleanPreferencesKey("live_wallpaper_enabled")
        val LIVE_WALLPAPER_STYLE = stringPreferencesKey("live_wallpaper_style")
        val USERNAME = stringPreferencesKey("username")
        val USE_24_HOUR_FORMAT = booleanPreferencesKey("use_24_hour_format")
        val LIVE_WALLPAPER_VIDEO_URI = stringPreferencesKey("live_wallpaper_video_uri")
        val LIVE_WALLPAPER_VIDEO_COLOR = intPreferencesKey("live_wallpaper_video_color")
        val LIVE_WALLPAPER_MUTED = booleanPreferencesKey("live_wallpaper_muted")
        val REMINDER_1H = booleanPreferencesKey("reminder_1h")
        val REMINDER_30M = booleanPreferencesKey("reminder_30m")
        val REMINDER_10M = booleanPreferencesKey("reminder_10m")
        val EVENT_NOTIFICATIONS = booleanPreferencesKey("event_notifications")
        val ALARM_BACKGROUND_URI = stringPreferencesKey("alarm_background_uri")
        val ALARM_BACKGROUND_TYPE = stringPreferencesKey("alarm_background_type")
    }

    object AlarmBackground {
        const val DEFAULT = "default"
        const val IMAGE = "image"
        const val VIDEO = "video"
    }

    val themeName: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.THEME_NAME] ?: "sakura" }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }
    val animeStyle: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.ANIME_STYLE] ?: "cute" }
    val particlesEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.PARTICLES_ENABLED] ?: true }
    val animationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.ANIMATIONS_ENABLED] ?: true }
    val customPrimaryColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.CUSTOM_PRIMARY_COLOR] ?: 0xFFFFB7C5.toInt() }
    val customSecondaryColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.CUSTOM_SECONDARY_COLOR] ?: 0xFF9C27B0.toInt() }
    val calendarView: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.CALENDAR_VIEW] ?: "month" }
    val showAnimeStickers: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.SHOW_ANIME_STICKERS] ?: true }
    val language: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.LANGUAGE] ?: systemDefaultLanguage() }
    val wallpaperHasImage: Flow<Boolean> = context.dataStore.data.map { (it[PreferencesKeys.WALLPAPER_FILENAME] ?: "").isNotBlank() }
    val wallpaperPrimaryColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.WALLPAPER_PRIMARY_COLOR] ?: 0xFFFFB7C5.toInt() }
    val wallpaperSecondaryColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.WALLPAPER_SECONDARY_COLOR] ?: 0xFF9C27B0.toInt() }
    val wallpaperAccentColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.WALLPAPER_ACCENT_COLOR] ?: 0xFF00BCD4.toInt() }
    val colorSource: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.COLOR_SOURCE] ?: ColorSource.THEME }
    val useWallpaperColors: Flow<Boolean> = colorSource.map { it == ColorSource.WALLPAPER }
    val liveWallpaperEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.LIVE_WALLPAPER_ENABLED] ?: false }
    val liveWallpaperStyle: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.LIVE_WALLPAPER_STYLE] ?: LiveWallpaper.NONE }
    val username: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USERNAME] ?: "" }
    val use24HourFormat: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.USE_24_HOUR_FORMAT] ?: true }
    val liveWallpaperVideoUri: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.LIVE_WALLPAPER_VIDEO_URI] ?: "" }
    val liveWallpaperVideoColor: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.LIVE_WALLPAPER_VIDEO_COLOR] ?: 0xFF7C4DFF.toInt() }
    val liveWallpaperMuted: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.LIVE_WALLPAPER_MUTED] ?: true }
    val reminder1h: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_1H] ?: false }
    val reminder30m: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_30M] ?: false }
    val reminder10m: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_10M] ?: true }
    val eventNotifications: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.EVENT_NOTIFICATIONS] ?: true }
    val alarmBackgroundUri: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.ALARM_BACKGROUND_URI] ?: "" }
    val alarmBackgroundType: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.ALARM_BACKGROUND_TYPE] ?: AlarmBackground.DEFAULT }

    suspend fun setThemeName(themeName: String) { context.dataStore.edit { it[PreferencesKeys.THEME_NAME] = themeName } }
    suspend fun setDarkMode(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = enabled } }
    suspend fun setAnimeStyle(style: String) { context.dataStore.edit { it[PreferencesKeys.ANIME_STYLE] = style } }
    suspend fun setParticlesEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.PARTICLES_ENABLED] = enabled } }
    suspend fun setAnimationsEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.ANIMATIONS_ENABLED] = enabled } }
    suspend fun setCustomPrimaryColor(color: Int) { context.dataStore.edit { it[PreferencesKeys.CUSTOM_PRIMARY_COLOR] = color } }
    suspend fun setCustomSecondaryColor(color: Int) { context.dataStore.edit { it[PreferencesKeys.CUSTOM_SECONDARY_COLOR] = color } }
    suspend fun setCalendarView(view: String) { context.dataStore.edit { it[PreferencesKeys.CALENDAR_VIEW] = view } }
    suspend fun setShowAnimeStickers(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.SHOW_ANIME_STICKERS] = enabled } }
    suspend fun setLanguage(language: String) { context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language } }
    suspend fun setUsername(name: String) { context.dataStore.edit { it[PreferencesKeys.USERNAME] = name.trim() } }
    suspend fun setUse24HourFormat(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.USE_24_HOUR_FORMAT] = enabled } }

    suspend fun setLiveWallpaperVideoUri(uri: String) {
        context.dataStore.edit {
            it[PreferencesKeys.LIVE_WALLPAPER_VIDEO_URI] = uri
            it[PreferencesKeys.LIVE_WALLPAPER_STYLE] = LiveWallpaper.VIDEO
            it[PreferencesKeys.LIVE_WALLPAPER_ENABLED] = true
        }
    }

    suspend fun setLiveWallpaperVideoColor(color: Int) {
        context.dataStore.edit { it[PreferencesKeys.LIVE_WALLPAPER_VIDEO_COLOR] = color }
    }

    suspend fun setLiveWallpaperMuted(muted: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.LIVE_WALLPAPER_MUTED] = muted }
    }

    suspend fun setReminder1h(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.REMINDER_1H] = enabled } }
    suspend fun setReminder30m(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.REMINDER_30M] = enabled } }
    suspend fun setReminder10m(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.REMINDER_10M] = enabled } }
    suspend fun setEventNotifications(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.EVENT_NOTIFICATIONS] = enabled } }

    suspend fun setAlarmBackground(type: String, uri: String) {
        context.dataStore.edit {
            it[PreferencesKeys.ALARM_BACKGROUND_TYPE] = type
            it[PreferencesKeys.ALARM_BACKGROUND_URI] = uri
        }
    }

    suspend fun setColorSource(source: String) {
        context.dataStore.edit { it[PreferencesKeys.COLOR_SOURCE] = source }
    }

    suspend fun setUseWallpaperColors(enabled: Boolean) {
        setColorSource(if (enabled) ColorSource.WALLPAPER else ColorSource.THEME)
    }

    suspend fun setLiveWallpaperEnabled(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.LIVE_WALLPAPER_ENABLED] = enabled
            if (enabled && (it[PreferencesKeys.LIVE_WALLPAPER_STYLE] ?: LiveWallpaper.NONE) == LiveWallpaper.NONE) {
                it[PreferencesKeys.LIVE_WALLPAPER_STYLE] = LiveWallpaper.GRADIENT
            }
        }
    }

    suspend fun setLiveWallpaperStyle(style: String) {
        context.dataStore.edit {
            it[PreferencesKeys.LIVE_WALLPAPER_STYLE] = style
            it[PreferencesKeys.LIVE_WALLPAPER_ENABLED] = style != LiveWallpaper.NONE
        }
    }

    suspend fun setWallpaperColors(primary: Int, secondary: Int, accent: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.WALLPAPER_PRIMARY_COLOR] = primary
            it[PreferencesKeys.WALLPAPER_SECONDARY_COLOR] = secondary
            it[PreferencesKeys.WALLPAPER_ACCENT_COLOR] = accent
        }
    }

    private fun saveWallpaperBitmap(bitmap: Bitmap): String {
        val filename = "amimin_wallpaper_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return filename
    }

    suspend fun getWallpaperBitmap(): Bitmap? {
        val filename = context.dataStore.data
            .map { it[PreferencesKeys.WALLPAPER_FILENAME] ?: "" }
            .first()
        if (filename.isBlank()) return null
        val file = File(context.filesDir, filename)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    suspend fun saveWallpaper(sourceUri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return false
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap != null) {
                val previous = context.dataStore.data
                    .map { it[PreferencesKeys.WALLPAPER_FILENAME] ?: "" }
                    .first()
                if (previous.isNotBlank()) {
                    File(context.filesDir, previous).delete()
                }
                val filename = saveWallpaperBitmap(bitmap)
                context.dataStore.edit { it[PreferencesKeys.WALLPAPER_FILENAME] = filename }
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun systemDefaultLanguage(): String {
        val sys = try {
            context.resources.configuration.locales[0].language
        } catch (e: Exception) {
            "en"
        }
        return if (sys in listOf("en", "es", "ja", "ko", "zh")) sys else "en"
    }

    suspend fun clearWallpaper() {
        val filename = context.dataStore.data
            .map { it[PreferencesKeys.WALLPAPER_FILENAME] ?: "" }
            .first()
        if (filename.isNotBlank()) {
            File(context.filesDir, filename).delete()
        }
        context.dataStore.edit {
            it[PreferencesKeys.WALLPAPER_FILENAME] = ""
            if (it[PreferencesKeys.COLOR_SOURCE] == ColorSource.WALLPAPER) {
                it[PreferencesKeys.COLOR_SOURCE] = ColorSource.THEME
            }
        }
    }

    suspend fun setRestoredWallpaper(filename: String) {
        context.dataStore.edit { it[PreferencesKeys.WALLPAPER_FILENAME] = filename }
    }
}
