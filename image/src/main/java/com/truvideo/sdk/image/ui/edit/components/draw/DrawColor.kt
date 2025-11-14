package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.animated_fade_visibility.TruvideoAnimatedFadeVisibility
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import com.truvideo.sdk.image.utils.ColorUtils
import kotlinx.collections.immutable.persistentListOf

@Composable
fun DrawColor2(
    color: Color = Color.White,
    onColorPressed: (Color) -> Unit = {}
) {
    val colors = remember { ColorUtils.colors }
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            colors.forEachIndexed { index, c ->
                Box(
                    modifier = Modifier.padding(start = if (index != 0) 4.dp else 0.dp)
                ) {
                    TruvideoScaleButton(
                        onPressed = { onColorPressed(c) }
                    ) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(2.dp, Color.White, CircleShape)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                TruvideoAnimatedFadeVisibility(c == color) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = getContrastColor(c),
                                        modifier = Modifier
                                            .size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            Modifier
                .width(16.dp)
                .height((40 + 16 + 16).dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = persistentListOf(
                            Color.Black,
                            Color.Black.copy(0.0f)
                        )
                    )
                )
        )

        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .width(16.dp)
                .height((40 + 16 + 16).dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = persistentListOf(
                            Color.Black.copy(0.0f),
                            Color.Black
                        )
                    )
                )
        )
    }
}

private fun getContrastColor(color: Color): Color {
    return if (color.luminance() > 0.5f) Color.Black else Color.White
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruVideoSdkTheme {
        Box(Modifier.background(Color.Black)) {
            DrawColor2()
        }
    }
}