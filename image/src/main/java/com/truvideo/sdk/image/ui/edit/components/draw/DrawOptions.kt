package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material.icons.outlined.VerticalAlignCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_content.TruvideoAnimatedContent
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton

private enum class Option {
    None,
    Rotation,
    Scale,
    Align
}

@Composable
internal fun DrawOptions(
    currentRotation: Float = 0f,
    currentScale: Float = 1.0f,
    onButtonBackPressed: () -> Unit = {},
    changeRotation: (Float) -> Unit = {},
    changeScale: (Float) -> Unit = {},
    changeAlignment: (Alignment) -> Unit = {},
    delete: () -> Unit = {}
) {
    var option by remember { mutableStateOf(Option.None) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TruvideoAnimatedContent(targetState = option) {
            when (it) {
                Option.None -> Content(
                    closeIcon = Icons.Outlined.Close,
                    onButtonBackPressed = onButtonBackPressed,
                    onButtonRotationPressed = { option = Option.Rotation },
                    onButtonScalePressed = { option = Option.Scale },
                    onButtonAlignmentPressed = { option = Option.Align },
                    onButtonDeletePressed = delete
                )

                Option.Rotation -> ContentRotation(
                    currentValue = currentRotation,
                    onButtonBackPressed = { option = Option.None },
                    change = changeRotation
                )

                Option.Scale -> ContentScale(
                    currentValue = currentScale,
                    onButtonBackPressed = { option = Option.None },
                    change = changeScale
                )

                Option.Align -> ContentAlignment(
                    onButtonBackPressed = { option = Option.None },
                    change = changeAlignment
                )
            }
        }
    }
}

@Composable
private fun Content(
    closeIcon: ImageVector = Icons.AutoMirrored.Outlined.Undo,
    onButtonBackPressed: () -> Unit = {},
    onButtonRotationPressed: () -> Unit = {},
    onButtonScalePressed: () -> Unit = {},
    onButtonAlignmentPressed: () -> Unit = {},
    onButtonDeletePressed: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(16.dp))

        TruvideoIconButton(
            icon = closeIcon,
            onPressed = onButtonBackPressed
        )

        Box(Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DrawOptionButton(
                icon = Icons.AutoMirrored.Outlined.RotateLeft,
                text = "Rotation",
                onPressed = onButtonRotationPressed
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                icon = Icons.Outlined.PhotoSizeSelectLarge,
                text = "Scale",
                onPressed = onButtonScalePressed
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                icon = Icons.Outlined.VerticalAlignCenter,
                text = "Alignment",
                onPressed = onButtonAlignmentPressed
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                icon = Icons.Outlined.Delete,
                text = "Delete",
                onPressed = onButtonDeletePressed
            )
        }
    }
}

@Composable
private fun ContentRotation(
    currentValue: Float = 0f,
    onButtonBackPressed: () -> Unit = {},
    change: (Float) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(16.dp))

        TruvideoIconButton(
            icon = Icons.AutoMirrored.Outlined.Undo,
            onPressed = onButtonBackPressed
        )

        Box(Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(16.dp))
            DrawOptionButton(
                icon = Icons.AutoMirrored.Outlined.RotateLeft,
                text = "To left",
                onPressed = { change(currentValue - 90f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                icon = Icons.AutoMirrored.Outlined.RotateRight,
                text = "To right",
                onPressed = { change(currentValue + 90f) }
            )
            Box(Modifier.width(16.dp))
            DrawOptionButton(
                text = "0º",
                onPressed = { change(0f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "90º",
                onPressed = { change(90f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "180º",
                onPressed = { change(180f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "270º",
                onPressed = { change(270f) }
            )
            Box(Modifier.width(16.dp))
        }
    }
}

@Composable
private fun ContentScale(
    currentValue: Float = 1.0f,
    onButtonBackPressed: () -> Unit = {},
    change: (Float) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(16.dp))

        TruvideoIconButton(
            icon = Icons.AutoMirrored.Outlined.Undo,
            onPressed = onButtonBackPressed
        )

        Box(Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(16.dp))
            DrawOptionButton(
                icon = Icons.Outlined.KeyboardDoubleArrowUp,
                text = "Scale up",
                onPressed = { change(currentValue * 1.25f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                icon = Icons.Outlined.KeyboardDoubleArrowDown,
                text = "Scale down",
                onPressed = { change(currentValue * 0.75f) }
            )
            Box(Modifier.width(16.dp))
            DrawOptionButton(
                text = "0.25x",
                onPressed = { change(0.25f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "0.5x",
                onPressed = { change(0.5f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "1.0x",
                onPressed = { change(1f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "1.5x",
                onPressed = { change(1.5f) }
            )
            Box(Modifier.width(4.dp))
            DrawOptionButton(
                text = "4x",
                onPressed = { change(4f) }
            )
            Box(Modifier.width(16.dp))
        }
    }
}


@Composable
private fun ContentAlignment(
    onButtonBackPressed: () -> Unit = {},
    change: (Alignment) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(16.dp))

        TruvideoIconButton(
            icon = Icons.AutoMirrored.Outlined.Undo,
            onPressed = onButtonBackPressed
        )

        Box(Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(16.dp))

            DrawOptionButton(
                text = "Top Left",
                onPressed = { change(Alignment.TopStart) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Top Center",
                onPressed = { change(Alignment.TopCenter) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Top Right",
                onPressed = { change(Alignment.TopEnd) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Center Left",
                onPressed = { change(Alignment.CenterStart) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Center",
                onPressed = { change(Alignment.Center) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Center Right",
                onPressed = { change(Alignment.CenterEnd) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Bottom Left",
                onPressed = { change(Alignment.BottomStart) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Bottom Center",
                onPressed = { change(Alignment.BottomCenter) }
            )

            Box(Modifier.width(4.dp))

            DrawOptionButton(
                text = "Bottom Right",
                onPressed = { change(Alignment.BottomEnd) }
            )

            Box(Modifier.width(16.dp))
        }
    }
}


@Composable
internal fun DrawOptionButton(
    icon: ImageVector? = null,
    text: String = "",
    onPressed: () -> Unit = {}
) {
    TruvideoScaleButton(
        onPressed = { onPressed() }
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(4.dp))
                .background(TruvideoColors.gray)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    modifier = Modifier.size(15.dp),
                    tint = Color.White
                )
                Box(Modifier.width(4.dp))
            }
            Text(text = text, color = Color.White)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    Column {
        DrawOptions()
        Content()
        ContentRotation()
        ContentScale()
        ContentAlignment()
    }
}