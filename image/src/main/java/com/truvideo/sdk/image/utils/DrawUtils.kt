package com.truvideo.sdk.image.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.truvideo.sdk.image.ui.edit.components.draw.DrawingData
import kotlin.math.ceil

internal object DrawUtils {

    fun createTextImage(
        text: String,
        size: Float,
        color: Color = Color.White,
        bold: Boolean = false,
        italic: Boolean = false
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = size
        paint.color = color.toArgb()
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(
            Typeface.DEFAULT, if (bold && italic) {
                Typeface.BOLD_ITALIC
            } else if (bold) {
                Typeface.BOLD
            } else if (italic) {
                Typeface.ITALIC
            } else {
                Typeface.NORMAL
            }
        )

        val textWidth = ceil(paint.measureText(text)).toInt().coerceAtLeast(1)
        val fontMetrics = paint.fontMetrics
        val textHeight = ceil(fontMetrics.bottom - fontMetrics.top).toInt().coerceAtLeast(1)
        val baseline = -fontMetrics.top

        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, baseline, paint)

        return bitmap
    }


    fun mergeImages(imageSize: Size, images: List<DrawingData>): Bitmap {
        val bitmap = Bitmap.createBitmap(imageSize.width.toInt(), imageSize.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        for (imageData in images) {
            val imageBitmap = imageData.bitmap
            val x = (imageData.position.x * imageSize.width)
            val y = (imageData.position.y * imageSize.height)
            val w = (imageData.size.width * imageSize.width)
            val h = (imageData.size.height * imageSize.height)

            canvas.save()
            canvas.translate(x, y)
            canvas.scale(imageData.scale, imageData.scale, w * 0.5f, h * 0.5f)
            canvas.rotate(imageData.rotation, w * 0.5f, h * 0.5f)
            canvas.drawBitmap(
                imageBitmap,
                Rect(0, 0, imageBitmap.width, imageBitmap.height),
                Rect(0, 0, w.toInt(), h.toInt()),
                paint
            )
            canvas.restore()
        }

        return bitmap
    }

    fun createImage(originalBitmap: Bitmap, images: List<DrawingData>): Bitmap {
        val bitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, Paint())

        val imageSize = Size(originalBitmap.width.toFloat(), originalBitmap.height.toFloat())
        val mergedImage = mergeImages(imageSize, images)
        canvas.drawBitmap(mergedImage, 0f, 0f, Paint())
        return bitmap
    }

}
