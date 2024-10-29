package com.truvideo.sdk.image.ui.edit.components.custom_gradient

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

@Composable
fun Modifier.horizontalGradient(colors: ImmutableList<Color>): Modifier {
    return background(
        brush = Brush.horizontalGradient(
            colors = colors
        )
    )
}