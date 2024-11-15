package com.truvideo.sdk.image.utils

import androidx.compose.ui.geometry.Offset
import com.truvideo.sdk.image.ui.edit.components.crop.model.CropInformation

internal object CropUtils {
    fun rotate(crop: CropInformation, angle: Float): CropInformation {
        require(angle in listOf(0f, 90f, 180f, 270f)) { "Angle must be one of 0, 90, 180, or 270 degrees" }

        return when (angle) {
            0f -> crop
            90f -> CropInformation(
                topLeft = Offset(crop.topLeft.y, 1.0f - crop.bottomRight.x),
                bottomRight = Offset(crop.bottomRight.y, 1.0f - crop.topLeft.x)
            )

            180f -> CropInformation(
                topLeft = Offset(1.0f - crop.bottomRight.x, 1.0f - crop.bottomRight.y),
                bottomRight = Offset(1.0f - crop.topLeft.x, 1.0f - crop.topLeft.y)
            )

            270f -> CropInformation(
                topLeft = Offset(1.0f - crop.bottomRight.y, crop.topLeft.x),
                bottomRight = Offset(1.0f - crop.topLeft.y, crop.bottomRight.x)
            )

            else -> crop
        }
    }

    fun flipHorizontal(crop: CropInformation): CropInformation {
        return CropInformation(
            topLeft = Offset(1.0f - crop.bottomRight.x, crop.topLeft.y),
            bottomRight = Offset(1.0f - crop.topLeft.x, crop.bottomRight.y)
        )
    }

    fun flipVertical(crop: CropInformation): CropInformation {
        return CropInformation(
            topLeft = Offset(crop.topLeft.x, 1.0f - crop.bottomRight.y),
            bottomRight = Offset(crop.bottomRight.x, 1.0f - crop.topLeft.y)
        )
    }
}