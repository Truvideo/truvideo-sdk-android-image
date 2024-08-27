package com.truvideo.sdk.image.ui.edit.activities.edit.model

import android.graphics.Bitmap
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation

private val dummyInformation = TruvideoSdkImageInformation(
    width = 600,
    height = 400,
    path = "",
    size = 0
)

internal data class PreviewData(
    val bitmap: Bitmap,
    val thumbnailBitmap: Bitmap,
    val thumbnailFilteredBitmap: Bitmap? = null,
    val information: TruvideoSdkImageInformation,
) {
    companion object {
        fun empty() = PreviewData(
            bitmap = Bitmap.createBitmap(dummyInformation.width, dummyInformation.height, Bitmap.Config.ARGB_8888),
            thumbnailBitmap = Bitmap.createBitmap(dummyInformation.width, dummyInformation.height, Bitmap.Config.ARGB_8888),
            information = dummyInformation,
        )
    }
}