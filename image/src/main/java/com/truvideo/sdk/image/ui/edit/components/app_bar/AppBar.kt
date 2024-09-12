package com.truvideo.sdk.image.ui.edit.components.app_bar

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.button.TruvideoContinueButton
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    buttonUndoVisible: Boolean = false,
    buttonUndoEnabled: Boolean = true,
    onButtonUndoPressed: (() -> Unit) = {},
    onButtonClosePressed: (() -> Unit) = {},
    buttonCloseEnabled: Boolean = true,
    onButtonContinuePressed: (() -> Unit) = {},
    buttonContinueEnabled: Boolean = true

) {
    Box(modifier) {
        Box(modifier.padding(16.dp)) {
            Row {
                TruvideoIconButton(
                    icon = Icons.Default.Close,
                    small = true,
                    enabled = buttonCloseEnabled,
                    onPressed = onButtonClosePressed
                )
                Box(Modifier.weight(1f))
                AnimatedContent(targetState = buttonUndoVisible, label = "undo") { visible ->
                    if (visible) {
                        TruvideoIconButton(
                            small = true,
                            enabled = buttonUndoEnabled,
                            icon = Icons.AutoMirrored.Outlined.Undo,
                            onPressed = { onButtonUndoPressed() }
                        )
                    } else {
                        Box(Modifier.height(30.dp))
                    }
                }
                Box(Modifier.width(8.dp))
                TruvideoContinueButton(
                    small = true,
                    enabled = buttonContinueEnabled,
                    onPressed = onButtonContinuePressed
                )
            }
        }
    }
}


@Composable
@Preview
private fun Test() {
    TruVideoSdkTheme {
        Box(Modifier.background(Color.Black)) {
            AppBar()
        }
    }
}