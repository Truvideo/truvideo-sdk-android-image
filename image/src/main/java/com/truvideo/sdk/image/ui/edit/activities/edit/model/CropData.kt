package com.truvideo.sdk.image.ui.edit.activities.edit.model

import com.truvideo.sdk.image.ui.edit.components.crop.model.CropInformation

internal data class CropData(
    val rotation: Float,
    val horizontalFlip: Boolean,
    val verticalFlip: Boolean,
    val information: CropInformation,
) {
    companion object {
        fun empty() = CropData(
            rotation = 0f,
            horizontalFlip = false,
            verticalFlip = false,
            information = CropInformation.full()
        )
    }
}
