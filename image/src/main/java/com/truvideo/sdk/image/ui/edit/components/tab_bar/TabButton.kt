package com.truvideo.sdk.image.ui.edit.components.tab_bar

import androidx.compose.animation.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton

@Composable
internal fun TabButton(
    enabled: Boolean = true,
    title: String,
    icon: ImageVector,
    selected: Boolean = false,
    onPressed: (() -> Unit)? = null
) {
    fun calculateIconColor(): Color {
        if (selected) return Color.White
        return Color.White.copy(0.5f)
    }

    val effectiveIconColor = remember { Animatable(calculateIconColor()) }
    LaunchedEffect(selected) { effectiveIconColor.animateTo(calculateIconColor()) }

    TruvideoScaleButton(
        enabled = enabled,
        onPressed = onPressed
    ) {
        Column(
            modifier = Modifier.width(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.size(28.dp)) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(effectiveIconColor.value)
                )
            }

            Text(
                text = title,
                color = effectiveIconColor.value,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview
private fun Test() {
    Box(Modifier.background(Color.Black)) {
        TabButton(
            title = "Button",
            selected = true,
            icon = Icons.Default.Home
        )
    }
}