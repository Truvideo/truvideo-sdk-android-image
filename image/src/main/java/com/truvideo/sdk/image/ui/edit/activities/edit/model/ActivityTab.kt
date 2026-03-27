package com.truvideo.sdk.image.ui.edit.activities.edit.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class ActivityTab(val displayName: String, val icon: ImageVector) {
    Crop("Crop", Icons.Outlined.Crop),
    Draw("Draw", Icons.Outlined.Draw),
//    Filter("Filters", Icons.Outlined.Tune)
}
