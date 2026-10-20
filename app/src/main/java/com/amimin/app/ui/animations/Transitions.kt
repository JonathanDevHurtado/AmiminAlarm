package com.amimin.app.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch

// Page Transition Animations
enum class TransitionType {
    SLIDE_HORIZONTAL,
    SLIDE_VERTICAL,
    FADE,
    SCALE,
    ROTATE,
    BOUNCE,
    ZOOM_FADE
}

@Composable
fun AnimatedScreenTransition(
    visible: Boolean,
    transitionType: TransitionType = TransitionType.FADE,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = when (transitionType) {
            TransitionType.SLIDE_HORIZONTAL -> slideInHorizontally(
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) { it / 3 } + fadeIn(tween(500))
            TransitionType.SLIDE_VERTICAL -> slideInVertically(
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) { it / 3 } + fadeIn(tween(500))
            TransitionType.FADE -> fadeIn(tween(500))
            TransitionType.SCALE -> scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(tween(300))
            TransitionType.ROTATE -> scaleIn(
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                initialScale = 0.5f
            ) + fadeIn(tween(500))
            TransitionType.BOUNCE -> slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) { it } + fadeIn(tween(300))
            TransitionType.ZOOM_FADE -> scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(500))
        },
        exit = when (transitionType) {
            TransitionType.SLIDE_HORIZONTAL -> slideOutHorizontally(
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) { -it / 3 } + fadeOut(tween(500))
            TransitionType.SLIDE_VERTICAL -> slideOutVertically(
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) { -it / 3 } + fadeOut(tween(500))
            TransitionType.FADE -> fadeOut(tween(500))
            TransitionType.SCALE -> scaleOut(
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300))
            TransitionType.ROTATE -> scaleOut(
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                targetScale = 0.5f
            ) + fadeOut(tween(500))
            TransitionType.BOUNCE -> slideOutVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) { -it } + fadeOut(tween(300))
            TransitionType.ZOOM_FADE -> scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(500))
        },
        content = content
    )
}

// Anime Card Animation
@Composable
fun Modifier.animeCardAnimation(
    isVisible: Boolean,
    index: Int = 0
): Modifier {
    val transition = updateTransition(targetState = isVisible, label = "cardTransition")

    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "scale"
    ) { visible ->
        if (visible) 1f else 0.8f
    }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(300, delayMillis = index * 100) },
        label = "alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }

    val offsetY by transition.animateIntOffset(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "offsetY"
    ) { visible ->
        if (visible) IntOffset(0, 0) else IntOffset(0, 50)
    }

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
        translationY = offsetY.y.toFloat()
    }
}

// Ripple Effect for Anime Touch
fun Modifier.animeRippleEffect(): Modifier = this

// Staggered Animation Helper
@Composable
fun <T> staggeredAnimation(
    items: List<T>,
    animationDelay: Int = 100,
    animationDuration: Int = 500
): List<Animatable<Float, AnimationVector1D>> {
    val animatables = remember {
        items.map { Animatable(0f) }
    }

    LaunchedEffect(items) {
        animatables.forEachIndexed { index, animatable ->
            launch {
                kotlinx.coroutines.delay(index * animationDelay.toLong())
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    return animatables
}
