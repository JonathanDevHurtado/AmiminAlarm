package com.amimin.app.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object LiveWallpaperStyle {
    const val NONE = "none"
    const val GRADIENT = "gradient"
    const val SAKURA = "sakura"
    const val STARS = "stars"
    const val AURORA = "aurora"
    const val VIDEO = "video"

    val all = listOf(
        NONE to "Ninguno",
        GRADIENT to "Degradado",
        SAKURA to "Sakura",
        STARS to "Estrellas",
        AURORA to "Aurora"
    )

    fun paletteFor(style: String): List<Color> = when (style) {
        SAKURA -> listOf(Color(0xFFFF7EA8), Color(0xFFE1A6E8))
        STARS -> listOf(Color(0xFF5C6BC0), Color(0xFF8E5BD9))
        AURORA -> listOf(Color(0xFF00D69A), Color(0xFF7C4DFF))
        GRADIENT -> listOf(Color(0xFF7C4DFF), Color(0xFF2F7BFF))
        else -> listOf(Color(0xFFFF6B9D), Color(0xFFFF8E53))
    }
}

@Composable
fun LiveWallpaperBackground(style: String, modifier: Modifier = Modifier) {
    when (style) {
        LiveWallpaperStyle.GRADIENT -> GradientLiveWallpaper(modifier)
        LiveWallpaperStyle.SAKURA -> SakuraLiveWallpaper(modifier)
        LiveWallpaperStyle.STARS -> StarsLiveWallpaper(modifier)
        LiveWallpaperStyle.AURORA -> AuroraLiveWallpaper(modifier)
        else -> {}
    }
}

@Composable
private fun GradientLiveWallpaper(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "gradient_live")
    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "shift"
    )
    val shift2 = transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "shift2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val s = shift.value
        val s2 = shift2.value
        val angle = 360f * s
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A0A3E),
                    lerpColor(Color(0xFF3D1A6E), Color(0xFF0D2A5C), s2),
                    lerpColor(Color(0xFF0D2A5C), Color(0xFF6E1A5A), s),
                    Color(0xFF2A0A3E)
                ),
                start = Offset(angle, angle),
                end = Offset(size.width + 400f - angle, size.height + 400f - angle)
            )
        )
    }
}

@Composable
private fun SakuraLiveWallpaper(modifier: Modifier = Modifier) {
    val petals = remember {
        List(40) {
            LivePetal(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.06f + 0.03f,
                drift = (Random.nextFloat() - 0.5f) * 0.04f,
                size = Random.nextFloat() * 10f + 6f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 90f,
                alpha = Random.nextFloat() * 0.5f + 0.3f,
                color = listOf(
                    Color(0xFFFFB7C5), Color(0xFFFFCDD2), Color(0xFFF8BBD0),
                    Color(0xFFFFE4EC), Color(0xFFE1BEE7)
                ).random()
            )
        }
    }

    var frame by remember { mutableLongStateOf(0L) }
    var lastNanos by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { now ->
                val dt = if (lastNanos == 0L) 0.016f else ((now - lastNanos) / 1_000_000_000f).coerceAtMost(0.05f)
                lastNanos = now
                petals.forEach { petal ->
                    petal.y += petal.speed * dt
                    petal.x += (petal.drift + sin(petal.y * 10f) * 0.02f) * dt
                    petal.rotation += petal.rotationSpeed * dt
                    if (petal.y > 1.15f) {
                        petal.y = -0.1f
                        petal.x = Random.nextFloat()
                    }
                    if (petal.x < -0.15f) petal.x = 1.1f
                    if (petal.x > 1.15f) petal.x = -0.1f
                }
                frame++
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        frame
        petals.forEach { petal ->
            val cx = size.width * petal.x
            val cy = size.height * petal.y
            val s = petal.size
            rotate(petal.rotation, Offset(cx, cy)) {
                val path = Path().apply {
                    moveTo(cx, cy - s)
                    cubicTo(cx + s, cy - s, cx + s, cy + s, cx, cy + s)
                    cubicTo(cx - s, cy + s, cx - s, cy - s, cx, cy - s)
                    close()
                }
                drawPath(path, petal.color.copy(alpha = petal.alpha))
            }
        }
    }
}

@Composable
private fun StarsLiveWallpaper(modifier: Modifier = Modifier) {
    val stars = remember {
        List(60) {
            LiveStar(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 3f,
                baseAlpha = Random.nextFloat() * 0.4f + 0.2f,
                phase = Random.nextFloat() * PI.toFloat() * 2f,
                twinkleSpeed = Random.nextFloat() * 2f + 0.8f,
                color = listOf(
                    Color.White, Color(0xFFFFF59D), Color(0xFFB3E5FC),
                    Color(0xFFF8BBD0), Color(0xFFE1BEE7)
                ).random()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "stars_time")
    val time = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = time.value
        stars.forEach { star ->
            val twinkle = (sin(t * star.twinkleSpeed * 0.05f + star.phase) + 1f) / 2f
            val alpha = (star.baseAlpha + twinkle * 0.6f).coerceIn(0f, 1f)
            drawStar(
                Offset(size.width * star.x, size.height * star.y),
                star.size, star.color.copy(alpha = alpha), 5
            )
        }
    }
}

@Composable
private fun AuroraLiveWallpaper(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val wave1 = transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave1"
    )
    val wave2 = transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave2"
    )
    val wave3 = transition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(17000, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave3"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF050A1E), Color(0xFF0A1030), Color(0xFF08122A))
            )
        )
        drawAuroraBand(size.width, size.height, Color(0xFF00FF88), wave1.value, 0.15f, 0.45f, 220f)
        drawAuroraBand(size.width, size.height, Color(0xFF00C9FF), wave2.value, 0.30f, 0.60f, 180f)
        drawAuroraBand(size.width, size.height, Color(0xFFB14EFF), wave3.value, 0.05f, 0.35f, 260f)
    }
}

private fun DrawScope.drawAuroraBand(
    width: Float,
    height: Float,
    color: Color,
    phase: Float,
    topFraction: Float,
    bottomFraction: Float,
    amplitude: Float
) {
    val path = Path()
    val topY = height * topFraction
    val bottomY = height * bottomFraction
    path.moveTo(0f, topY)
    val steps = 40
    for (i in 0..steps) {
        val x = width * i / steps
        val wave = sin((i.toFloat() / steps + phase) * PI.toFloat() * 2f) * amplitude
        path.lineTo(x, topY + wave)
    }
    for (i in steps downTo 0) {
        val x = width * i / steps
        val wave = sin((i.toFloat() / steps + phase) * PI.toFloat() * 2f + 1f) * amplitude
        path.lineTo(x, bottomY + wave)
    }
    path.close()
    drawPath(
        path,
        brush = Brush.verticalGradient(
            listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.06f), Color.Transparent),
            startY = topY,
            endY = bottomY
        )
    )
}

private class LivePetal(
    var x: Float, var y: Float, val speed: Float, val drift: Float,
    val size: Float, var rotation: Float, val rotationSpeed: Float,
    val alpha: Float, val color: Color
)

private class LiveStar(
    val x: Float, val y: Float, val size: Float, val baseAlpha: Float,
    val phase: Float, val twinkleSpeed: Float, val color: Color
)

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f
)
