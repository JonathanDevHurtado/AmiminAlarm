package com.amimin.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SakuraPink, secondary = AnimePurple, tertiary = AnimeCyan,
    background = DarkBackground, surface = DarkSurface, surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground, onSecondary = DarkBackground, onTertiary = DarkBackground,
    onBackground = DarkOnBackground, onSurface = DarkOnSurface, onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = SakuraPinkDark, secondary = AnimePurple, tertiary = AnimeCyan,
    background = LightBackground, surface = LightSurface, surfaceVariant = LightSurfaceVariant,
    onPrimary = LightBackground, onSecondary = LightBackground, onTertiary = LightBackground,
    onBackground = LightOnBackground, onSurface = LightOnSurface, onSurfaceVariant = LightOnSurfaceVariant
)

fun getColorSchemeFromThemeName(themeName: String, darkTheme: Boolean): androidx.compose.material3.ColorScheme {
    val palette = AnimeThemePalettes.fromName(themeName)
    return if (darkTheme) {
        darkColorScheme(
            primary = palette.primary, secondary = palette.secondary, tertiary = palette.accent,
            background = DarkBackground, surface = DarkSurface, surfaceVariant = DarkSurfaceVariant,
            onPrimary = DarkOnBackground, onSecondary = DarkOnBackground, onTertiary = DarkOnBackground,
            onBackground = DarkOnBackground, onSurface = DarkOnSurface, onSurfaceVariant = DarkOnSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = palette.primaryDark, secondary = palette.secondary, tertiary = palette.accent,
            background = palette.background, surface = palette.surface, surfaceVariant = LightSurfaceVariant,
            onPrimary = Color.White, onSecondary = Color.White, onTertiary = Color.White,
            onBackground = palette.onBackground, onSurface = palette.onSurface, onSurfaceVariant = LightOnSurfaceVariant
        )
    }
}

fun getCustomColorScheme(primaryColor: Int, secondaryColor: Int, darkTheme: Boolean): androidx.compose.material3.ColorScheme {
    val primary = Color(primaryColor)
    val secondary = Color(secondaryColor)
    return if (darkTheme) {
        darkColorScheme(
            primary = primary, secondary = secondary, tertiary = secondary,
            background = DarkBackground, surface = DarkSurface, surfaceVariant = DarkSurfaceVariant,
            onPrimary = DarkOnBackground, onSecondary = DarkOnBackground, onTertiary = DarkOnBackground,
            onBackground = DarkOnBackground, onSurface = DarkOnSurface, onSurfaceVariant = DarkOnSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = primary, secondary = secondary, tertiary = secondary,
            background = LightBackground, surface = LightSurface, surfaceVariant = LightSurfaceVariant,
            onPrimary = Color.White, onSecondary = Color.White, onTertiary = Color.White,
            onBackground = LightOnBackground, onSurface = LightOnSurface, onSurfaceVariant = LightOnSurfaceVariant
        )
    }
}

// Font families and typography for different anime styles
object AnimeStyleFonts {
    val kawaii = FontFamily.Default
    val elegant = FontFamily.Serif
    val cyberpunk = FontFamily.Monospace
    val minimal = FontFamily.SansSerif
    val retro = FontFamily.Cursive

    fun fromStyle(style: String): FontFamily = when (style) {
        "elegant" -> elegant
        "cyberpunk" -> cyberpunk
        "minimal" -> minimal
        "retro" -> retro
        else -> kawaii
    }
}

private data class StyleSpec(
    val weight: FontWeight,
    val scale: Float,
    val letterSpacing: Double
)

private fun specFor(style: String): StyleSpec = when (style) {
    "elegant" -> StyleSpec(FontWeight.Normal, 1.06f, 2.0)
    "cyberpunk" -> StyleSpec(FontWeight.Bold, 0.94f, 0.8)
    "minimal" -> StyleSpec(FontWeight.Light, 0.9f, 0.0)
    "retro" -> StyleSpec(FontWeight.Bold, 1.02f, 1.4)
    else -> StyleSpec(FontWeight.Bold, 1.0f, 0.3)
}

private fun ts(spec: StyleSpec, family: FontFamily, base: Float, weight: FontWeight? = null, ls: Double? = null): TextStyle =
    TextStyle(
        fontFamily = family,
        fontWeight = weight ?: spec.weight,
        fontSize = (base * spec.scale).sp,
        lineHeight = (base * spec.scale * 1.25f).sp,
        letterSpacing = (ls ?: spec.letterSpacing).sp
    )

fun getTypographyForStyle(style: String): Typography {
    val fontFamily = AnimeStyleFonts.fromStyle(style)
    val spec = specFor(style)
    return Typography(
        displayLarge = ts(spec, fontFamily, 57f, FontWeight.Bold, -0.25),
        displayMedium = ts(spec, fontFamily, 45f, FontWeight.Bold),
        displaySmall = ts(spec, fontFamily, 36f, FontWeight.SemiBold),
        headlineLarge = ts(spec, fontFamily, 32f, FontWeight.Bold),
        headlineMedium = ts(spec, fontFamily, 28f, FontWeight.SemiBold),
        headlineSmall = ts(spec, fontFamily, 24f, FontWeight.Medium),
        titleLarge = ts(spec, fontFamily, 22f, FontWeight.Bold),
        titleMedium = ts(spec, fontFamily, 16f, FontWeight.SemiBold),
        titleSmall = ts(spec, fontFamily, 14f, FontWeight.Medium),
        bodyLarge = ts(spec, fontFamily, 16f, FontWeight.Normal, 0.5),
        bodyMedium = ts(spec, fontFamily, 14f, FontWeight.Normal),
        bodySmall = ts(spec, fontFamily, 12f, FontWeight.Normal, 0.4),
        labelLarge = ts(spec, fontFamily, 14f, FontWeight.Medium),
        labelMedium = ts(spec, fontFamily, 12f, FontWeight.Medium, 0.5),
        labelSmall = ts(spec, fontFamily, 11f, FontWeight.Normal, 0.5)
    )
}

@Composable
fun AmiminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    animeStyle: String = "cute",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = getTypographyForStyle(animeStyle)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = AnimeShapes, content = content)
}

@Composable
fun AmiminThemeWithWallpaper(
    primaryColor: Color, secondaryColor: Color, accentColor: Color,
    darkTheme: Boolean = isSystemInDarkTheme(),
    animeStyle: String = "cute",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(primary = primaryColor, secondary = secondaryColor, tertiary = accentColor,
            background = DarkBackground, surface = DarkSurface, surfaceVariant = DarkSurfaceVariant,
            onPrimary = DarkBackground, onSecondary = DarkBackground, onTertiary = DarkBackground,
            onBackground = DarkOnBackground, onSurface = DarkOnSurface, onSurfaceVariant = DarkOnSurfaceVariant)
    } else {
        lightColorScheme(primary = primaryColor, secondary = secondaryColor, tertiary = accentColor,
            background = LightBackground, surface = LightSurface, surfaceVariant = LightSurfaceVariant,
            onPrimary = Color.White, onSecondary = Color.White, onTertiary = Color.White,
            onBackground = LightOnBackground, onSurface = LightOnSurface, onSurfaceVariant = LightOnSurfaceVariant)
    }
    val typography = getTypographyForStyle(animeStyle)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = AnimeShapes, content = content)
}

@Composable
fun AmiminThemeFromPalette(
    themeName: String,
    darkTheme: Boolean = isSystemInDarkTheme(),
    animeStyle: String = "cute",
    content: @Composable () -> Unit
) {
    val colorScheme = getColorSchemeFromThemeName(themeName, darkTheme)
    val typography = getTypographyForStyle(animeStyle)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = AnimeShapes, content = content)
}

@Composable
fun AmiminThemeCustomColors(
    primaryColor: Int,
    secondaryColor: Int,
    darkTheme: Boolean = isSystemInDarkTheme(),
    animeStyle: String = "cute",
    content: @Composable () -> Unit
) {
    val colorScheme = getCustomColorScheme(primaryColor, secondaryColor, darkTheme)
    val typography = getTypographyForStyle(animeStyle)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = AnimeShapes, content = content)
}

@Composable
fun AmiminAnimeTheme(
    themeColors: AnimeThemeColors,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(primary = themeColors.primary, secondary = themeColors.secondary, tertiary = themeColors.accent,
            background = themeColors.background, surface = themeColors.surface,
            onPrimary = themeColors.onSurface, onSecondary = themeColors.onSurface,
            onBackground = themeColors.onBackground, onSurface = themeColors.onSurface)
    } else {
        lightColorScheme(primary = themeColors.primaryDark, secondary = themeColors.secondary, tertiary = themeColors.accent,
            background = themeColors.background, surface = themeColors.surface,
            onPrimary = themeColors.background, onSecondary = themeColors.background,
            onBackground = themeColors.onBackground, onSurface = themeColors.onSurface)
    }

    MaterialTheme(colorScheme = colorScheme, typography = AmiminTypography, shapes = AnimeShapes, content = content)
}
