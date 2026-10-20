package com.amimin.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Anime Colors
val SakuraPink = Color(0xFFFFB7C5)
val SakuraPinkDark = Color(0xFFE91E63)
val SakuraPinkLight = Color(0xFFFFE4EC)
val AnimePurple = Color(0xFF9C27B0)
val AnimePurpleLight = Color(0xFFCE93D8)
val AnimeBlue = Color(0xFF2196F3)
val AnimeBlueLight = Color(0xFF90CAF9)
val AnimeCyan = Color(0xFF00BCD4)
val AnimeCyanLight = Color(0xFF80DEEA)
val AnimeGreen = Color(0xFF4CAF50)
val AnimeGreenLight = Color(0xFFA5D6A7)
val AnimeOrange = Color(0xFFFF9800)
val AnimeOrangeLight = Color(0xFFFFCC80)
val AnimeRed = Color(0xFFF44336)
val AnimeRedLight = Color(0xFFEF9A9A)
val AnimeYellow = Color(0xFFFFEB3B)
val AnimeYellowLight = Color(0xFFFFF9C4)

// Dark Theme Colors
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkOnBackground = Color(0xFFE0E0E0)
val DarkOnSurface = Color(0xFFEEEEEE)
val DarkOnSurfaceVariant = Color(0xFFBDBDBD)

// Light Theme Colors
val LightBackground = Color(0xFFFFFBFE)
val LightSurface = Color(0xFFF5F5F5)
val LightSurfaceVariant = Color(0xFFEEEEEE)
val LightOnBackground = Color(0xFF1C1B1F)
val LightOnSurface = Color(0xFF1C1B1F)
val LightOnSurfaceVariant = Color(0xFF49454F)

// Gradient Colors for Anime Effects
val AnimeGradientStart = Color(0xFFFF6B9D)
val AnimeGradientMiddle = Color(0xFFFF8E53)
val AnimeGradientEnd = Color(0xFFFFBE76)

val CosmicGradientStart = Color(0xFF667EEA)
val CosmicGradientMiddle = Color(0xFF764BA2)
val CosmicGradientEnd = Color(0xFF6B73FF)

val SunsetGradientStart = Color(0xFFFF6B6B)
val SunsetGradientMiddle = Color(0xFFFF8E53)
val SunsetGradientEnd = Color(0xFFFFBE76)

val OceanGradientStart = Color(0xFF00C9FF)
val OceanGradientMiddle = Color(0xFF92FE9D)
val OceanGradientEnd = Color(0xFF00F260)

// Preset Anime Theme Palettes
object AnimeThemePalettes {
    val Sakura = AnimeThemeColors(
        primary = SakuraPink,
        primaryDark = SakuraPinkDark,
        primaryLight = SakuraPinkLight,
        secondary = AnimePurple,
        secondaryLight = AnimePurpleLight,
        accent = AnimeCyan,
        background = Color(0xFFFFF0F5),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF2C1810),
        onSurface = Color(0xFF2C1810)
    )

    val Midnight = AnimeThemeColors(
        primary = AnimePurple,
        primaryDark = Color(0xFF7B1FA2),
        primaryLight = AnimePurpleLight,
        secondary = AnimeCyan,
        secondaryLight = AnimeCyanLight,
        accent = AnimeBlue,
        background = Color(0xFF0D0221),
        surface = Color(0xFF1A0A3E),
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFE0E0E0)
    )

    val Cherry = AnimeThemeColors(
        primary = AnimeRed,
        primaryDark = Color(0xFFC62828),
        primaryLight = AnimeRedLight,
        secondary = AnimeOrange,
        secondaryLight = AnimeOrangeLight,
        accent = AnimeYellow,
        background = Color(0xFFFFF5F5),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )

    val Ocean = AnimeThemeColors(
        primary = AnimeBlue,
        primaryDark = Color(0xFF1565C0),
        primaryLight = AnimeBlueLight,
        secondary = AnimeCyan,
        secondaryLight = AnimeCyanLight,
        accent = AnimeGreen,
        background = Color(0xFFF0F8FF),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )

    val Neon = AnimeThemeColors(
        primary = Color(0xFF00FF88),
        primaryDark = Color(0xFF00CC6A),
        primaryLight = Color(0xFF66FFB3),
        secondary = Color(0xFFFF00FF),
        secondaryLight = Color(0xFFFF66FF),
        accent = Color(0xFF00FFFF),
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF1A1A2E),
        onBackground = Color(0xFF00FF88),
        onSurface = Color(0xFF00FF88)
    )

    val Pastel = AnimeThemeColors(
        primary = Color(0xFFFFB3BA),
        primaryDark = Color(0xFFFF8FA3),
        primaryLight = Color(0xFFFFD4DB),
        secondary = Color(0xFFB5EAD7),
        secondaryLight = Color(0xFFD4F0E7),
        accent = Color(0xFFC7CEEA),
        background = Color(0xFFFFF5EE),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF4A4A4A),
        onSurface = Color(0xFF4A4A4A)
    )

    fun fromName(name: String): AnimeThemeColors = when (name) {
        "sakura" -> Sakura
        "midnight" -> Midnight
        "cherry" -> Cherry
        "ocean" -> Ocean
        "neon" -> Neon
        "pastel" -> Pastel
        else -> Sakura
    }
}

data class AnimeThemeColors(
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val secondary: Color,
    val secondaryLight: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val onBackground: Color,
    val onSurface: Color
)
