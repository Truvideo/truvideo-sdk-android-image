package com.truvideo.sdk.image.ui.edit.components.custom_animated

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.Animatable as AnimateColor
import androidx.compose.animation.core.Animatable as AnimateFloat

@Composable
fun animateColor(
    value: Color,
    spec: AnimationSpec<Color>? = null,
    animate: Boolean = true
): Color {
    val animation = remember { AnimateColor(value) }
    val animationValue by remember { derivedStateOf { animation.value } }
    var currentColor by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (currentColor == value) return@LaunchedEffect
        currentColor = value

        if (animate) {
            if (spec != null) {
                animation.animateTo(value, spec)
            } else {
                animation.animateTo(value)
            }
        } else {
            animation.snapTo(value)
        }
    }

    return if (animate) {
        animationValue
    } else {
        value
    }
}

@Composable
fun animateFloat(
    value: Float,
    spec: AnimationSpec<Float>? = null,
    animate: Boolean = true
): Float {
    val animation = remember { AnimateFloat(value) }
    val animationValue by remember { derivedStateOf { animation.value } }
    var currentValue by remember { mutableFloatStateOf(value) }
    LaunchedEffect(value) {
        if (currentValue == value) return@LaunchedEffect
        currentValue = value

        if (animate) {
            if (spec != null) {
                animation.animateTo(value, spec)
            } else {
                animation.animateTo(value)
            }
        } else {
            animation.snapTo(value)
        }
    }

    return if (animate) {
        animationValue
    } else {
        value
    }
}

val springAnimationFloatSpec: SpringSpec<Float> = SpringSpec(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessLow,
    visibilityThreshold = null
)

val springAnimationColorSpec: SpringSpec<Color> = SpringSpec(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessLow,
    visibilityThreshold = null
)