package com.truvideo.sdk.image.ui.edit.components.animated_fit

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.animated_value.animateFloat
import com.truvideo.sdk.components.animated_value.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedRotatedAspectRatio(
    spec: AnimationSpec<Float> = springAnimationFloatSpec,
    aspectRatio: Float,
    rotation: Float = 0f,
    animate: Boolean = true,
    content: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current

    val aspectRatioAnim = animateFloat(
        value = aspectRatio,
        spec = spec,
        animate = animate
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val containerWidth = context.dpToPx(this.maxWidth.value)
        val containerHeight = context.dpToPx(this.maxHeight.value)
        val parentAspectRatio = containerWidth / containerHeight

        val rotationAnim = animateFloat(
            value = rotation,
            spec = spec
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)

        ) {
            val (contentWidth, contentHeight) = if (parentAspectRatio > aspectRatioAnim) {
                val adjustedHeight = context.dpToPx(this.maxHeight.value)
                val adjustedWidth = adjustedHeight * aspectRatioAnim
                Pair(adjustedWidth, adjustedHeight)
            } else {
                val adjustedWidth = context.dpToPx(this.maxWidth.value)
                val adjustedHeight = adjustedWidth / aspectRatioAnim
                Pair(adjustedWidth, adjustedHeight)
            }
            val (rotatedWidth, rotatedHeight) = getRotatedDimensions(
                width = contentWidth,
                height = contentHeight,
                rotation = rotation
            )
            val rotatedAspectRatio = rotatedWidth / rotatedHeight

            val scale = if (parentAspectRatio > rotatedAspectRatio) {
                (containerHeight / rotatedHeight)
            } else {
                (containerWidth / rotatedWidth)
            }

            val scaleAnim = animateFloat(
                value = scale,
                spec = spec
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.rotationZ = rotationAnim
                        this.scaleX = scaleAnim
                        this.scaleY = scaleAnim
                    }
                    .aspectRatio(aspectRatioAnim)
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                if (content != null) {
                    content()
                }
            }
        }
    }
}


private fun getRotatedDimensions(width: Float, height: Float, rotation: Float): Pair<Float, Float> {
    val radians = Math.toRadians(rotation.toDouble())
    val rotatedWidth = (abs(width * cos(radians)) + abs(height * sin(radians))).toFloat()
    val rotatedHeight = (abs(width * sin(radians)) + abs(height * cos(radians))).toFloat()
    return Pair(rotatedWidth, rotatedHeight)
}

@Composable
@Preview(showBackground = true)
private fun Test() {

    var animate by remember { mutableStateOf(true) }
    var rotation by remember { mutableFloatStateOf(90f) }
    var contentAspectRatio by remember { mutableFloatStateOf(3 / 2f) }
    var borderVisible by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clipToBounds()
        ) {

            AnimatedRotatedAspectRatio(
                animate = animate,
                aspectRatio = contentAspectRatio,
                rotation = rotation,
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Text("TL", color = Color.White, modifier = Modifier.align(Alignment.TopStart))
                    Text("TR", color = Color.White, modifier = Modifier.align(Alignment.TopEnd))
                    Text("BL", color = Color.White, modifier = Modifier.align(Alignment.BottomStart))
                    Text("BR", color = Color.White, modifier = Modifier.align(Alignment.BottomEnd))
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = borderVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.Cyan.copy(0.5f))
                    )
                }
            }
        }

        Text("Animate: $animate", modifier = Modifier.clickable {
            animate = !animate
        })

        Text("Border: $borderVisible", modifier = Modifier.clickable {
            borderVisible = !borderVisible
        })

        Text("Aspect Ratio: $contentAspectRatio", modifier = Modifier.clickable {
            contentAspectRatio = 1 / contentAspectRatio
        })

        Text("Rotation: $rotation", modifier = Modifier.clickable {
            rotation += 90f
        })

        Text("Clear rotation", modifier = Modifier.clickable {
            rotation = 0f
        })
    }

}