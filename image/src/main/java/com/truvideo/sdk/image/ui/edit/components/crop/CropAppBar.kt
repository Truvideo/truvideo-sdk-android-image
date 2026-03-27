package com.truvideo.sdk.image.ui.edit.components.crop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.animated_fade_visibility.TruvideoAnimatedFadeVisibility
import com.truvideo.sdk.components.animated_rotation.TruvideoAnimatedRotation
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
internal fun CropAppBar(
    undoVisible: Boolean = true,
    onButtonUndoPressed: () -> Unit = {},
    onButtonRotateLeftPressed: () -> Unit = {},
    onButtonRotateRightPressed: () -> Unit = {},
    onButtonFlipHorizontalPressed: () -> Unit = {},
    onButtonFlipVerticalPressed: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TruvideoAnimatedFadeVisibility(undoVisible) {
            TruvideoIconButton(
                small = true,
                identifier = "undo_btn",
                icon = Icons.AutoMirrored.Outlined.Undo,
                onPressed = { onButtonUndoPressed() }
            )
        }
        Box(Modifier.weight(1f))
        TruvideoIconButton(
            small = true,
            identifier = "rotate_left_btn",
            icon = Icons.AutoMirrored.Outlined.RotateLeft,
            onPressed = { onButtonRotateLeftPressed() }
        )
        Box(Modifier.width(8.dp))
        TruvideoIconButton(
            small = true,
            icon = Icons.AutoMirrored.Outlined.RotateRight,
            onPressed = { onButtonRotateRightPressed() },
            identifier = "rotate_right_btn"
        )
        Box(Modifier.width(8.dp))
        TruvideoIconButton(
            small = true,
            icon = Icons.Outlined.Flip,
            onPressed = { onButtonFlipHorizontalPressed() },
            identifier = "flip_horizontal_btn"
        )
        Box(Modifier.width(8.dp))
        TruvideoAnimatedRotation(-90f) {
            TruvideoIconButton(
                small = true,
                icon = Icons.Outlined.Flip,
                onPressed = { onButtonFlipVerticalPressed() },
                identifier = "flip_vertical_btn"
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruVideoSdkTheme {
        Box(Modifier.background(Color.Black)){
            CropAppBar()
        }
    }
}