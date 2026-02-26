package com.truvideo.sdk.image.ui.edit.activities.edit.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation

private val dummyInformation = TruvideoSdkImageInformation(
    width = 2000,
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

        private fun createBitmap(w: Int, h:Int):Bitmap{
            val result = Bitmap.createBitmap(
                w,
                h,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(result)
            val paint = Paint()
            paint.color = Color.RED
            canvas.drawRect(
                0f,
                0f,
                result.width.toFloat(),
                result.height.toFloat(),
                paint
            )

            return result
        }

        fun empty(): PreviewData = PreviewData(
            bitmap = createBitmap(dummyInformation.width, dummyInformation.height),
            thumbnailBitmap = createBitmap(dummyInformation.width, dummyInformation.height),
            information = dummyInformation,
        )
    }
}