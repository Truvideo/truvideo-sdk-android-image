package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.image.ui.edit.activities.edit.model.DrawMode


@Composable
internal fun DrawAppBar(
    undoVisible: Boolean = true,
    buttonPickerVisible: Boolean = true,
    drawMode: DrawMode? = DrawMode.Pencil,
    onButtonUndoPressed: () -> Unit = {},
    onButtonPencilPressed: () -> Unit = {},
    onButtonPickerPressed: () -> Unit = {},
    onButtonTextPressed: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(targetState = undoVisible, label = "undo-visible") { visible ->
            if (visible) {
                TruvideoIconButton(
                    small = true,
                    icon = Icons.AutoMirrored.Outlined.Undo,
                    onPressed = { onButtonUndoPressed() }
                )
            } else {
                Box(Modifier.height(30.dp))
            }
        }

        Box(Modifier.weight(1f))

        TruvideoIconButton(
            small = true,
            icon = Icons.Outlined.Edit,
            selected = drawMode == DrawMode.Pencil,
            onPressed = { onButtonPencilPressed() }
        )

        Box(Modifier.width(8.dp))

        TruvideoIconButton(
            small = true,
            icon = Icons.Outlined.TextFields,
            onPressed = { onButtonTextPressed() }
        )

        AnimatedContent(targetState = buttonPickerVisible, label = "picker-visible") { visible ->
            if (visible) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(8.dp))

                    TruvideoIconButton(
                        small = true,
                        icon = Icons.Outlined.SelectAll,
                        selected = drawMode == DrawMode.Picker,
                        onPressed = { onButtonPickerPressed() }
                    )
                }
            } else {
                Box(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var drawMode by remember { mutableStateOf(DrawMode.Pencil) }
    var undoVisible by remember { mutableStateOf(true) }

    Column {

        Box(Modifier.background(Color.Black)) {
            DrawAppBar(
                drawMode = drawMode,
                onButtonPencilPressed = { drawMode = DrawMode.Pencil },
                onButtonPickerPressed = { drawMode = DrawMode.Picker },
                onButtonUndoPressed = { },
                undoVisible = undoVisible
            )
        }
        Text("Undo visible: $undoVisible", Modifier.clickable {
            undoVisible = !undoVisible
        })
    }
}