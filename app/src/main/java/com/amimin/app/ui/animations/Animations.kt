package com.amimin.app.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.random.Random

fun Modifier.shimmerEffect(
    shimmerColor: Color = Color.White.copy(alpha = 0.6f),
    animationDuration: Int = 1000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
        ),
        label = "shimmerTranslate"
    )
    Modifier.drawBehind {
        val gradient = Brush.linearGradient(
            colors = listOf(Color.Transparent, shimmerColor, Color.Transparent),
            start = Offset(translateX, 0f),
            end = Offset(translateX + size.width * 0.5f, size.height)
        )
        drawRect(brush = gradient)
    }
}

fun Modifier.pulseEffect(animationDuration: Int = 1500): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulseScale"
    )
    val alpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulseAlpha"
    )
    Modifier.graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
}

fun Modifier.floatingEffect(amplitude: Float = 10f, animationDuration: Int = 3000): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "floating")
    val offsetY by transition.animateFloat(
        initialValue = -amplitude, targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "floatingOffset"
    )
    Modifier.graphicsLayer { translationY = offsetY }
}

fun Modifier.rotatingEffect(animationDuration: Int = 2000): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "rotating")
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(animationDuration, easing = LinearEasing)),
        label = "rotation"
    )
    Modifier.graphicsLayer { rotationZ = rotation }
}

fun Modifier.bounceEffect(animationDuration: Int = 600): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "bounce")
    val offsetY by transition.animateFloat(
        initialValue = 0f, targetValue = -20f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration / 2, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "bounceOffset"
    )
    Modifier.graphicsLayer { translationY = offsetY }
}

fun Modifier.glowEffect(glowColor: Color = Color(0xFFFF69B4), animationDuration: Int = 2000): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "glowAlpha"
    )
    Modifier.drawBehind {
        drawCircle(
            color = glowColor.copy(alpha = glowAlpha),
            radius = size.maxDimension * 0.8f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

data class Sparkle(val x: Float, val y: Float, val size: Float, val color: Color, val delay: Float)

@Composable
fun rememberSparkles(
    count: Int = 15,
    colors: List<Color> = listOf(Color(0xFFFFD700), Color(0xFFFF69B4), Color(0xFF00FFFF), Color(0xFFFF6B6B))
): List<Sparkle> {
    return remember {
        List(count) {
            Sparkle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 8f + 4f, colors.random(), Random.nextFloat() * 2000f)
        }
    }
}

fun Modifier.sparkleEffect(sparkles: List<Sparkle>, animationDuration: Int = 3000): Modifier = composed {
    val alphas = sparkles.map { sparkle ->
        val transition = rememberInfiniteTransition(label = "sparkle_${sparkle.x}_${sparkle.y}")
        val alpha by transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(animationDuration, delayMillis = sparkle.delay.toInt(), easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ), label = "sparkleAlpha"
        )
        alpha
    }
    Modifier.drawBehind {
        sparkles.forEachIndexed { index, sparkle ->
            drawCircle(
                color = sparkle.color.copy(alpha = alphas.getOrElse(index) { 0.5f }),
                radius = sparkle.size,
                center = Offset(size.width * sparkle.x, size.height * sparkle.y)
            )
        }
    }
}

fun Modifier.animatedGradientEffect(colors: List<Color>, animationDuration: Int = 5000): Modifier = composed {
    Modifier.drawBehind {
        val gradient = Brush.sweepGradient(
            colors = colors,
            center = Offset(size.width * 0.5f, size.height * 0.5f)
        )
        drawRect(brush = gradient)
    }
}
