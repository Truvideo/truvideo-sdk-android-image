package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.min
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_value.animateColor
import com.truvideo.sdk.components.animated_value.springAnimationColorSpec
import com.truvideo.sdk.components.animated_value.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.components.crop.pxToDp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.horizontal_slider.HorizontalSlider
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
fun DrawWidth(
    value: Float = 1.0f,
    color: Color = Color.White,
    minValue: Float = 1f,
    maxValue: Float = 20f,
    onValueChange: (Float) -> Unit = {}
) {
    HorizontalSlider(
        value = value,
        minValue = minValue,
        maxValue = maxValue,
        onValueChange = onValueChange
    ){
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

@Composable
@Preview(showBackground = true)
private fun Test() {
    var value by remember { mutableFloatStateOf(4f) }
    var color by remember { mutableStateOf(Color.White) }

    TruVideoSdkTheme {
        Column {
            Box(Modifier.background(Color.Black)) {
                DrawWidth(
                    value = value,
                    onValueChange = { value = it },
                    color = color
                )
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