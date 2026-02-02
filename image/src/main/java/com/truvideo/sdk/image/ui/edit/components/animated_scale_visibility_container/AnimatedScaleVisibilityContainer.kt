package com.truvideo.sdk.image.ui.edit.components.animated_scale_visibility_container

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import kotlinx.coroutines.delay

@Composable
fun AnimatedScaleVisibilityContainer(
    visible: Boolean = true,
    openDelay: Long = 0L,
    closeDelay: Long = 0L,
    content: @Composable () -> Unit
) {

    var currentVisible by remember { mutableStateOf(visible) }

    LaunchedEffect(visible) {
        if (visible) {
            delay(openDelay)
            currentVisible = true
        } else {
            delay(closeDelay)
            currentVisible = false
        }
    }

    val scale = animateFloat(
        value = if (currentVisible) 1f else 0.7f,
        spec = springAnimationFloatSpec
    )
    val opacity = animateFloat(
        value = if (currentVisible) 1f else 0f,
        spec = springAnimationFloatSpec
    ).coerceIn(0f, 1f)

    Box(Modifier) {
        Box(Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = opacity
        }) {
            if (opacity > 0) {
                content()
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var visible by remember { mutableStateOf(true) }

    TruVideoSdkTheme {
        Column {
            AnimatedScaleVisibilityContainer(
                visible = visible,
                openDelay = 300
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Red)
                )
            }

            Text("Visible: $visible", Modifier.clickable { visible = !visible })
        }
    }
}