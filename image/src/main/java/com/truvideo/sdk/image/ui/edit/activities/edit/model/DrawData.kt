package com.truvideo.sdk.image.ui.edit.activities.edit.model

import com.truvideo.sdk.image.ui.edit.components.draw.DrawingData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal data class DrawData(
    val images: PersistentList<DrawingData>,
) {
    companion object {
        fun empty() = DrawData(
            images = persistentListOf()
        )
    }
}