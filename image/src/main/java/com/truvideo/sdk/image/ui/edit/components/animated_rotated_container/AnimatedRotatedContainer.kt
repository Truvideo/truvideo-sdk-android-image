package com.truvideo.sdk.image.ui.edit.components.animated_rotated_container

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
fun AnimatedRotatedContainer(
    rotation: Float = 0f,
    animate: Boolean = true,
    content: @Composable () -> Unit
) {
    val rotationAnim = animateFloat(
        value = rotation,
        animate = animate,
        spec = springAnimationFloatSpec
    )

    Box(Modifier.graphicsLayer {
        rotationZ = rotationAnim
    }) {
        content()
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var rotation by remember { mutableFloatStateOf(0f) }

    TruVideoSdkTheme {
        Column {
            AnimatedRotatedContainer(
                rotation = rotation
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .background(Color.Red)
                )
            }
            Text("Rotation: $rotation", Modifier.clickable {
                rotation = when (rotation) {
                    0f -> 90f
                    90f -> 180f
                    180f -> 270f
                    else -> 0f
                }
            })
        }
    }
}