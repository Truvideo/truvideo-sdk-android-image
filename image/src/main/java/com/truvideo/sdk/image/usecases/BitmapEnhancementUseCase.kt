package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.truvideo.sdk.image.model.ImageEnhancementType
import com.truvideo.sdk.image.model.ImageFilterType
import com.truvideo.sdk.image.utils.ColorMatrixUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine


internal class BitmapEnhancementUseCase {

    val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(bitmap: Bitmap, type: ImageEnhancementType, intensity: Float = 1f): Bitmap {
        return suspendCancellableCoroutine {
            scope.launch {
                val colorMatrix = when (type) {
                    ImageEnhancementType.Exposicion -> TODO()
                    ImageEnhancementType.Luminocidad -> TODO()
                    ImageEnhancementType.Luces -> TODO()
                    ImageEnhancementType.Sombras -> TODO()
                    ImageEnhancementType.Contraste -> TODO()
                    ImageEnhancementType.PuntoNegro -> TODO()
                    ImageEnhancementType.Saturation -> TODO()
                    ImageEnhancementType.Vivacidad -> TODO()
                    ImageEnhancementType.Calidez -> TODO()
                    ImageEnhancementType.Tinte -> TODO()
                }

                val newBitmap = applyColorTransform(
                    bitmap = bitmap,
                    colorMatrix = colorMatrix,
                    intensity = intensity
                )

                val end = System.currentTimeMillis()
                it.resumeWith(Result.success(newBitmap))
            }
        }
    }



    private fun applyColorTransform(bitmap: Bitmap, colorMatrix: ColorMatrix, intensity: Float): Bitmap {
        val identityMatrix = ColorMatrix()
        val matrix = ColorMatrix().apply {
            val blendMatrix = FloatArray(20)
            for (i in blendMatrix.indices) {
                blendMatrix[i] = identityMatrix.array[i] * (1 - intensity) + colorMatrix.array[i] * intensity
            }
            set(blendMatrix)
        }


        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val result = Bitmap.createBitmap(width, height, bitmap.config)

        val canvas = Canvas(result)
        val paint = Paint().apply {
            this.colorFilter = ColorMatrixColorFilter(matrix)

        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }
}