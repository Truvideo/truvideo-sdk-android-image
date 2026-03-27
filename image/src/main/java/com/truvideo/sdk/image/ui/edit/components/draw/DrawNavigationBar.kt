package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.truvideo.sdk.components.button.TruvideoButton
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme


@Composable
internal fun DrawNavigationBar(
    enabled: Boolean = true,
    onButtonApplyPressed: (() -> Unit) = {},
    onButtonCancelPressed: (() -> Unit) = {},
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                TruvideoButton(
                    enabled = enabled,
                    text = "Cancel",
                    identifier = "cancel_btn",
                    fullWidth = true,
                    color = Color.Transparent,
                    textColor = Color.White,
                    borderColor = Color.White.copy(0.4f),
                    borderWidth = 2f,
                    onPressed = { onButtonCancelPressed() }
                )
            }
            Box(Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                TruvideoButton(
                    enabled = enabled,
                    text = "Accept",
                    identifier = "accept_btn",
                    fullWidth = true,
                    selected = true,
                    onPressed = { onButtonApplyPressed() }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var enabled by remember { mutableStateOf(true) }

    TruVideoSdkTheme {
        Column {
            Box(Modifier.background(Color.Black)) {
                DrawNavigationBar(
                    enabled = enabled
                )
            }
            Text("Enabled: $enabled", Modifier.clickable { enabled = !enabled })
        }

    }
}