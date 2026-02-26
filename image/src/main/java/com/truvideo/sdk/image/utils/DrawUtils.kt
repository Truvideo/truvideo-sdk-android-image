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

    // Offset to prevent cuts for Italic fonts
    private const val OFFSET = 4
    // Multiplier to give more room for the offset
    private const val OFFSET_MULTIPLIER = 2

    fun createTextImage(
        text: String,
        size: Float,
        color: Color = Color.White,
        bold: Boolean = false,
        italic: Boolean = false
    ): Bitmap {
        if (text.isEmpty()) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            this.color = color.toArgb()
            textSize = size
            isFilterBitmap = true
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(
                Typeface.DEFAULT,
                when {
                    bold && italic -> Typeface.BOLD_ITALIC
                    bold -> Typeface.BOLD
                    italic -> Typeface.ITALIC
                    else -> Typeface.NORMAL
                }
            )
        }

        val fontMetrics = paint.fontMetrics

        val width = ceil(paint.measureText(text)).toInt().coerceAtLeast(1) + (OFFSET * OFFSET_MULTIPLIER)
        val height = ceil(fontMetrics.descent - fontMetrics.ascent).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val baseline = -fontMetrics.ascent
        canvas.drawText(text, OFFSET.toFloat(), baseline, paint)

        return bitmap
    }

    /**
     * Draws directly on the result Bitmap to save memory.
     */
    fun createImage(originalBitmap: Bitmap, images: List<DrawingData>): Bitmap {
        val resultBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            originalBitmap.config ?: Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(resultBitmap)
        val basePaint = Paint(Paint.FILTER_BITMAP_FLAG)

        canvas.drawBitmap(originalBitmap, 0f, 0f, basePaint)

        val imageSize = Size(originalBitmap.width.toFloat(), originalBitmap.height.toFloat())
        drawLayersOnCanvas(canvas, imageSize, images)

        return resultBitmap
    }

    /**
     * Draws the layers on the existing Canvas to save memory.
     */
    private fun drawLayersOnCanvas(canvas: Canvas, canvasSize: Size, layers: List<DrawingData>) {
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        for (data in layers) {
            val imageBitmap = data.bitmap
            if (imageBitmap.isRecycled) continue

            val x = (data.position.x * canvasSize.width)
            val y = (data.position.y * canvasSize.height)
            val w = (data.size.width * canvasSize.width)
            val h = (data.size.height * canvasSize.height)

            canvas.save()
            canvas.translate(x, y)
            canvas.scale(data.scale, data.scale, w * 0.5f, h * 0.5f)
            canvas.rotate(data.rotation, w * 0.5f, h * 0.5f)

            val destRect = Rect(0, 0, w.toInt(), h.toInt())
            canvas.drawBitmap(
                imageBitmap,
                null,
                destRect,
                paint
            )
            canvas.restore()
        }
    }
}
