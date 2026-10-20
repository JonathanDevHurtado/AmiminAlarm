package com.amimin.app

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.amimin.app.data.repository.SettingsRepository
import com.amimin.app.data.repository.SettingsRepository.ColorSource
import com.amimin.app.data.repository.SettingsRepository.LiveWallpaper
import com.amimin.app.ui.animations.LiveWallpaperBackground
import com.amimin.app.ui.animations.VideoLiveWallpaper
import com.amimin.app.ui.navigation.AppNavigation
import com.amimin.app.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun attachBaseContext(newBase: Context) {
        val stored = newBase.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, null)
        val lang = stored ?: systemDefaultLanguage(newBase)
        val locale = localeFor(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentActivity = WeakReference(this)

        requestNotificationPermissionIfNeeded()

        setContent {
            val isDarkMode by settingsRepository.isDarkMode.collectAsState(initial = false)
            val themeName by settingsRepository.themeName.collectAsState(initial = "sakura")
            val colorSource by settingsRepository.colorSource.collectAsState(initial = ColorSource.THEME)
            val wallpaperPrimary by settingsRepository.wallpaperPrimaryColor.collectAsState(initial = 0xFFFFB7C5.toInt())
            val wallpaperSecondary by settingsRepository.wallpaperSecondaryColor.collectAsState(initial = 0xFF9C27B0.toInt())
            val wallpaperAccent by settingsRepository.wallpaperAccentColor.collectAsState(initial = 0xFF00BCD4.toInt())
            val wallpaperHasImage by settingsRepository.wallpaperHasImage.collectAsState(initial = false)
            val customPrimaryColor by settingsRepository.customPrimaryColor.collectAsState(initial = 0xFFFFB7C5.toInt())
            val customSecondaryColor by settingsRepository.customSecondaryColor.collectAsState(initial = 0xFF9C27B0.toInt())
            val animeStyle by settingsRepository.animeStyle.collectAsState(initial = "cute")
            val liveWallpaperEnabled by settingsRepository.liveWallpaperEnabled.collectAsState(initial = false)
            val liveWallpaperStyle by settingsRepository.liveWallpaperStyle.collectAsState(initial = LiveWallpaper.NONE)
            val liveWallpaperVideoUri by settingsRepository.liveWallpaperVideoUri.collectAsState(initial = "")
            val liveWallpaperMuted by settingsRepository.liveWallpaperMuted.collectAsState(initial = true)
            val navController = rememberNavController()

            var wallpaperBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

            LaunchedEffect(wallpaperHasImage) {
                wallpaperBitmap = withContext(Dispatchers.IO) {
                    if (wallpaperHasImage) settingsRepository.getWallpaperBitmap() else null
                }
            }

            val baseScheme = when {
                colorSource == ColorSource.WALLPAPER && wallpaperHasImage -> {
                    wallpaperColorScheme(
                        Color(wallpaperPrimary), Color(wallpaperSecondary), Color(wallpaperAccent), isDarkMode
                    )
                }
                colorSource == ColorSource.CUSTOM -> {
                    getCustomColorScheme(customPrimaryColor, customSecondaryColor, isDarkMode)
                }
                else -> getColorSchemeFromThemeName(themeName, isDarkMode)
            }

            val hasLiveWallpaper = liveWallpaperEnabled && liveWallpaperStyle != LiveWallpaper.NONE
            val hasAnyBackground = hasLiveWallpaper || wallpaperBitmap != null

            val colorScheme = if (hasAnyBackground) {
                baseScheme.copy(background = Color.Transparent, surface = baseScheme.surface.copy(alpha = 0.72f))
            } else {
                baseScheme
            }

            val typography = getTypographyForStyle(animeStyle)

            MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = AnimeShapes) {
                val view = LocalView.current
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = colorScheme.background.toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
                }

                Box(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
                    when {
                        hasLiveWallpaper && liveWallpaperStyle == LiveWallpaper.VIDEO && liveWallpaperVideoUri.isNotBlank() -> {
                            VideoLiveWallpaper(
                                uriString = liveWallpaperVideoUri,
                                muted = liveWallpaperMuted,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        hasLiveWallpaper -> {
                            LiveWallpaperBackground(style = liveWallpaperStyle, modifier = Modifier.fillMaxSize())
                        }
                        wallpaperBitmap != null -> {
                            Image(
                                bitmap = wallpaperBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                        if (hasAnyBackground) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(alpha = 0.45f),
                                                Color.Black.copy(alpha = 0.25f),
                                                Color.Black.copy(alpha = 0.55f)
                                            )
                                        )
                                    )
                            )
                        }
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    private fun wallpaperColorScheme(
        primary: Color, secondary: Color, accent: Color, darkTheme: Boolean
    ): androidx.compose.material3.ColorScheme {
        return if (darkTheme) {
            androidx.compose.material3.darkColorScheme(
                primary = primary, secondary = secondary, tertiary = accent,
                background = Color.Transparent, surface = DarkSurface, surfaceVariant = DarkSurfaceVariant,
                onPrimary = DarkBackground, onSecondary = DarkBackground, onTertiary = DarkBackground,
                onBackground = DarkOnBackground, onSurface = DarkOnSurface, onSurfaceVariant = DarkOnSurfaceVariant
            )
        } else {
            androidx.compose.material3.lightColorScheme(
                primary = primary, secondary = secondary, tertiary = accent,
                background = Color.Transparent, surface = LightSurface, surfaceVariant = LightSurfaceVariant,
                onPrimary = Color.White, onSecondary = Color.White, onTertiary = Color.White,
                onBackground = LightOnBackground, onSurface = LightOnSurface, onSurfaceVariant = LightOnSurfaceVariant
            )
        }
    }

    companion object {
        private const val PREFS = "amimin_locale"
        private const val KEY_LANG = "language"
        private var currentActivity: WeakReference<Activity>? = null

        fun changeLanguage(context: Context, langCode: String) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_LANG, langCode).apply()
            Locale.setDefault(localeFor(langCode))
            currentActivity?.get()?.recreate()
        }

        private fun localeFor(langCode: String): Locale = when (langCode) {
            "es" -> Locale("es")
            "ja" -> Locale("ja")
            "ko" -> Locale("ko")
            "zh" -> Locale("zh")
            "ru" -> Locale("ru")
            "fr" -> Locale("fr")
            "de" -> Locale("de")
            else -> Locale("en")
        }

        private fun systemDefaultLanguage(context: Context): String {
            val sys = try {
                context.resources.configuration.locales[0].language
            } catch (e: Exception) {
                "en"
            }
            return if (sys in listOf("en", "es", "ja", "ko", "zh", "ru", "fr", "de")) sys else "en"
        }
    }
}
