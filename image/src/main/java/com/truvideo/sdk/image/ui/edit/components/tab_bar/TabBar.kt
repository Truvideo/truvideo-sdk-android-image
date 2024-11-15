package com.truvideo.sdk.image.ui.edit.components.tab_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.image.ui.edit.activities.edit.model.ActivityTab
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
internal fun TabBar(
    enabled: Boolean = true,
    tab: ActivityTab? = null,
    bottomPadding: Dp = 0.dp,
    onChange: ((tab: ActivityTab?) -> Unit) = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
            .background(TruvideoColors.gray)
            .fillMaxWidth()
            .padding(bottom = bottomPadding)
            .height(70.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActivityTab.entries.forEach {
            TabButton(
                enabled = enabled,
                title = it.displayName,
                selected = tab == it,
                icon = it.icon,
                onPressed = { onChange(it) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruVideoSdkTheme {
        TabBar(
            tab = null
        )
    }
}