package com.amimin.app.ui.animations

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Petal(
    var x: Float,
    var y: Float,
    val speed: Float,
    val drift: Float,
    val size: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val alpha: Float,
    val color: Color,
    val wobble: Float
) {
    fun update(dt: Float, width: Float, height: Float) {
        y += speed * dt * height
        x += (drift + sin(y * 0.02f) * wobble) * dt * width
        rotation += rotationSpeed * dt
        if (y > height + size * 2) {
            y = -size * 2
            x = Random.nextFloat() * width
        }
        if (x < -size * 2) x = width + size
        if (x > width + size * 2) x = -size
    }
}

@Composable
fun SakuraPetalSystem(modifier: Modifier = Modifier, petalCount: Int = 30) {
    val colors = remember {
        listOf(Color(0xFFFFB7C5), Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFFFE4EC))
    }
    val petals = remember {
        List(petalCount) {
            Petal(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.06f + 0.03f,
                drift = (Random.nextFloat() - 0.3f) * 0.04f,
                size = Random.nextFloat() * 6f + 4f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 90f,
                alpha = 0.55f + Random.nextFloat() * 0.4f,
                color = colors.random(),
                wobble = Random.nextFloat() * 0.03f + 0.01f
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
                val w = 1f
                val h = 1f
                petals.forEach { it.update(dt, w, h) }
                frame++
            }
        }
    }

    Canvas(modifier = modifier) {
        frame
        val width = size.width
        val height = size.height
        petals.forEach { petal ->
            val cx = petal.x * width
            val cy = petal.y * height
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

private class Bubble(
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val alpha: Float
) {
    fun update(dt: Float, width: Float, height: Float) {
        y -= speed * dt * height
        if (y < -size * 2) {
            y = height + size * 2
            x = Random.nextFloat() * width
        }
    }
}

@Composable
fun ParticleSystem(
    modifier: Modifier = Modifier,
    particleCount: Int = 30,
    colors: List<Color> = listOf(Color(0xFFFFB7C5), Color(0xFFCE93D8), Color(0xFF80DEEA), Color(0xFFFFCC80))
) {
    val particles = remember {
        List(particleCount) {
            Bubble(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.05f + 0.02f,
                size = Random.nextFloat() * 8f + 3f,
                color = colors.random(),
                alpha = 0.4f + Random.nextFloat() * 0.5f
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
                particles.forEach { it.update(dt, 1f, 1f) }
                frame++
            }
        }
    }

    Canvas(modifier = modifier) {
        frame
        val width = size.width
        val height = size.height
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(particle.x * width, particle.y * height)
            )
        }
    }
}

private class TwinkleStar(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val phase: Float,
    val speed: Float
)

@Composable
fun StarAnimation(modifier: Modifier = Modifier, starCount: Int = 14) {
    val palette = remember {
        listOf(Color(0xFFFFD700), Color(0xFFFF69B4), Color(0xFF00FFFF), Color(0xFFFF6B6B), Color(0xFF9C27B0))
    }
    val stars = remember {
        List(starCount) {
            TwinkleStar(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 10f + 5f,
                color = palette.random(),
                phase = Random.nextFloat() * 6.28f,
                speed = Random.nextFloat() * 2f + 1f
            )
        }
    }

    var frame by remember { mutableLongStateOf(0L) }
    var elapsed by remember { mutableFloatStateOf(0f) }
    var lastNanos by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { now ->
                val dt = if (lastNanos == 0L) 0.016f else ((now - lastNanos) / 1_000_000_000f).coerceAtMost(0.05f)
                lastNanos = now
                elapsed += dt
                frame++
            }
        }
    }

    Canvas(modifier = modifier) {
        frame
        val width = size.width
        val height = size.height
        stars.forEach { star ->
            val alpha = ((sin(elapsed * star.speed + star.phase) + 1f) / 2f) * 0.8f + 0.2f
            drawStar(
                Offset(star.x * width, star.y * height),
                star.size,
                star.color.copy(alpha = alpha),
                5
            )
        }
    }
}

internal fun DrawScope.drawStar(center: Offset, radius: Float, color: Color, points: Int) {
    val path = Path()
    val angle = (2 * Math.PI / points).toFloat()
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else radius * 0.4f
        val x = center.x + r * cos(angle * i - Math.PI.toFloat() / 2)
        val y = center.y + r * sin(angle * i - Math.PI.toFloat() / 2)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}
