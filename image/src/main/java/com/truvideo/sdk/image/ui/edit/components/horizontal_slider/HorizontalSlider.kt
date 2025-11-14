package com.truvideo.sdk.image.ui.edit.components.horizontal_slider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_value.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.components.crop.pxToDp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
fun HorizontalSlider(
    value: Float = 1.0f,
    minValue: Float = 1f,
    maxValue: Float = 20f,
    onValueChange: (Float) -> Unit = {},
    previewContent: (@Composable (value: Float) -> Unit)? = null,
) {
    val context = LocalContext.current

    var dragging by remember { mutableStateOf(false) }
    var percentage by remember {
        mutableFloatStateOf(
            scaleFloat(
                value = value,
                fromMin = minValue,
                fromMax = maxValue,
                toMin = 0f,
                toMax = 1f
            )
        )
    }

    LaunchedEffect(value) {
        if (dragging) return@LaunchedEffect
        percentage = scaleFloat(
            value = value,
            fromMin = minValue,
            fromMax = maxValue,
            toMin = 0f,
            toMax = 1f
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        BoxWithConstraints(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val w = context.dpToPx(maxWidth.value)
            val x = scaleFloat(
                value = percentage,
                fromMin = 0f,
                fromMax = 1f,
                toMin = 0f,
                toMax = context.pxToDp(w - context.dpToPx(30f))
            )

            val animX = animateFloat(
                value = x,
                animate = !dragging,
                spec = springAnimationFloatSpec,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(TruvideoColors.gray)
            )

            Box(
                modifier = Modifier
                    .width(x.dp + 15.dp)
                    .height(4.dp)
                    .background(TruvideoColors.amber)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                dragging = true
                                val tapped = tryAwaitRelease()
                                if (tapped) {
                                    dragging = false
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { dragging = true },
                            onDragEnd = { dragging = false },
                            onDragCancel = { dragging = false }
                        ) { change, dragAmount ->
                            percentage = (((percentage * w) + dragAmount.x) / w).coerceIn(0f, 1f)
                            onValueChange(
                                scaleFloat(
                                    value = percentage,
                                    fromMin = 0f,
                                    fromMax = 1f,
                                    toMin = minValue,
                                    toMax = maxValue
                                )
                            )
                            change.consume()
                        }
                    }
            )

            Box(modifier = Modifier.size(30.dp)) {
                Box(
                    modifier = Modifier
                        .offset(animX.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }


        if (previewContent != null) {
            Box(modifier = Modifier.width(16.dp))
            previewContent(value)
        }
    }
}

internal fun scaleFloat(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (value - fromMin) / (fromMax - fromMin) * (toMax - toMin) + toMin
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var value by remember { mutableFloatStateOf(4f) }
    var color by remember { mutableStateOf(Color.White) }

    TruVideoSdkTheme {
        Column {
            Box(Modifier.background(Color.Black)) {
                HorizontalSlider(
                    value = value,
                    onValueChange = { value = it },
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(it.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }

            Text("Value: $value")
            Text("Color: $color", modifier = Modifier.clickable {
                color = when (color) {
                    Color.White -> Color.Black
                    else -> Color.White
                }
            })

        }
    }
}