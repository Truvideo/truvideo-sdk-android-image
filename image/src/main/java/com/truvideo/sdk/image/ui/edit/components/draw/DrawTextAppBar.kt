package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.button.TruvideoButton
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
fun DrawTextAppBar(
    onButtonAddPressed: () -> Unit = {},
    onButtonClearPressed: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TruvideoIconButton(
            icon = Icons.Outlined.Clear,
            onPressed = { onButtonClearPressed() },
            identifier = "clear_btn",
        )
        Box(Modifier.weight(1f))
        TruvideoButton(
            leftIcon = Icons.Outlined.Check,
            fullWidth = false,
            text = "Add",
            onPressed = { onButtonAddPressed() },
            identifier = "add_btn"
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruVideoSdkTheme {
        Box(Modifier.background(Color.Black)) {
            DrawTextAppBar()
        }
    }
}