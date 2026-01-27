package com.truvideo.sdk.image.ui.edit.components.crop.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

internal data class CropInformation(
    val topLeft: Offset,
    val bottomRight: Offset
) {
    companion object {
        fun full(): CropInformation {
            return CropInformation(
                topLeft = Offset(0.0f, 0.0f),
                bottomRight = Offset(1.0f, 1.0f)
            )
        }
    }

    val width: Float
        get() {
            return bottomRight.x - topLeft.x
        }

    val height: Float
        get() {
            return bottomRight.y - topLeft.y
        }

    fun calculateAspectRatio(imageSize: Size): Float {
        val w = calculateWidth(imageSize.width)
        val h = calculateHeight(imageSize.height)
        return w / h
    }

    fun calculateWidth(imageWith: Float): Float = imageWith * width

    fun calculateHeight(imageHeight: Float): Float = imageHeight * height

}
