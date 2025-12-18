package com.truvideo.sdk.image.ui.edit.components.animated_slide_visibility_component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import kotlinx.coroutines.delay

enum class AnimatedSlideVisibilityDirection {
    Left,
    Top,
    Right,
    Bottom
}

val AnimatedSlideVisibilityDirection.isVertical: Boolean
    get() = this == AnimatedSlideVisibilityDirection.Top || this == AnimatedSlideVisibilityDirection.Bottom

@Composable
fun AnimatedSlideVisibilityComponent(
    visible: Boolean = true,
    animated: Boolean = true,
    clip: Boolean = false,
    openDelay: Long = 0L,
    closeDelay: Long = 0L,
    multiplier: Float = 1f,
    direction: AnimatedSlideVisibilityDirection = AnimatedSlideVisibilityDirection.Bottom,
    content: @Composable () -> Unit
) {
    var currentVisibility by remember(animated) { mutableStateOf(visible) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val targetY = if (currentVisibility) 0f else when (direction) {
        AnimatedSlideVisibilityDirection.Top -> -size.height * multiplier
        AnimatedSlideVisibilityDirection.Bottom -> size.height * multiplier
        else -> 0f
    }
    val y = animateFloat(
        value = targetY,
        animate = animated,
        spec = springAnimationFloatSpec
    )

    val targetX = if (currentVisibility) 0f else when (direction) {
        AnimatedSlideVisibilityDirection.Left -> -size.width * multiplier
        AnimatedSlideVisibilityDirection.Right -> size.width * multiplier
        else -> 0f
    }
    val x = animateFloat(
        value = targetX,
        animate = animated,
        spec = springAnimationFloatSpec
    )

    LaunchedEffect(visible) {
        if (animated) {
            if (visible) {
                delay(openDelay)
            } else {
                delay(closeDelay)
            }
        }
        currentVisibility = visible
    }

    var modifier = Modifier.onSizeChanged { size = it }
    if (clip) {
        modifier = modifier.clipToBounds()
    }

    Box(modifier) {
        Box(Modifier.graphicsLayer {
            translationY = y
            translationX = x
        }) {
            content()
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var visible by remember { mutableStateOf(true) }
    var animated by remember { mutableStateOf(true) }

    TruVideoSdkTheme {
        Column {

            AnimatedSlideVisibilityComponent(
                visible = visible,
                animated = animated,
                clip = true
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Red)
                )
            }

            Text("Visible: $visible", Modifier.clickable { visible = !visible })
            Text("Animated: $animated", Modifier.clickable { animated = !animated })
        }

    }
}