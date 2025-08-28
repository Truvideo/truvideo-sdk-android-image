package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.truvideo.sdk.image.model.ImageFilterType
import com.truvideo.sdk.image.utils.ColorMatrixUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine


internal class BitmapFilterUseCase {

    val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(bitmap: Bitmap, type: ImageFilterType, intensity: Float = 1f): Bitmap {
        return suspendCancellableCoroutine {
            scope.launch {

                val start = System.currentTimeMillis()

                val colorMatrix = when (type) {
                    ImageFilterType.Default -> {
                        it.resumeWith(Result.success(bitmap))
                        return@launch
                    }

                    ImageFilterType.Vivid -> getVividColorMatrix()
                    ImageFilterType.VividWarm -> getVividWarmColorMatrix()
                    ImageFilterType.VividCold -> getVividColdColorMatrix()
                    ImageFilterType.Dramatic -> getDramaticColorFilter()
                    ImageFilterType.DramaticWarm -> getDramaticWarmColorMatrix()
                    ImageFilterType.DramaticCold -> getDramaticColdColorMatrix()
                    ImageFilterType.Sepia -> getSepiaColorMatrix()
                    ImageFilterType.Warm -> getWarmColorMatrix()
                    ImageFilterType.Cold -> getColdColorMatrix()
                    ImageFilterType.Grayscale -> getGrayScaleColorMatrix()
                    ImageFilterType.Noir -> getNoirColorMatrix()
                    ImageFilterType.InverseColor -> getInverseColorMatrix()
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


    private fun getVividColorMatrix(): ColorMatrix {
        val saturationMatrix = ColorMatrixUtils.saturateMatrix(1.5f)
        val contrastMatrix = ColorMatrixUtils.contrastMatrix(1.2f)
        return ColorMatrixUtils.concat(saturationMatrix, contrastMatrix)
    }

    private fun getVividWarmColorMatrix(): ColorMatrix {
        val vividMatrix = getVividColorMatrix()
        val warmMatrix = ColorMatrixUtils.warmMatrix(0.1f)
        return ColorMatrixUtils.concat(vividMatrix, warmMatrix)
    }

    private fun getVividColdColorMatrix(): ColorMatrix {
        val vividMatrix = getVividColorMatrix()
        val warmMatrix = ColorMatrixUtils.warmMatrix(-0.1f)
        return ColorMatrixUtils.concat(vividMatrix, warmMatrix)
    }

    private fun getDramaticColorFilter(): ColorMatrix {
        val saturationMatrix = ColorMatrixUtils.saturateMatrix(0.5f)
        val contrastMatrix = ColorMatrixUtils.contrastMatrix(1.5f)
        return ColorMatrixUtils.concat(saturationMatrix, contrastMatrix)
    }

    private fun getDramaticWarmColorMatrix(): ColorMatrix {
        val dramaticMatrix = getDramaticColorFilter()
        val warmMatrix = ColorMatrixUtils.warmMatrix(0.1f)
        return ColorMatrixUtils.concat(dramaticMatrix, warmMatrix)
    }

    private fun getDramaticColdColorMatrix(): ColorMatrix {
        val dramaticMatrix = getDramaticColorFilter()
        val warmMatrix = ColorMatrixUtils.warmMatrix(-0.1f)
        return ColorMatrixUtils.concat(dramaticMatrix, warmMatrix)
    }

    private fun getSepiaColorMatrix(): ColorMatrix {
        return ColorMatrix().apply {
            set(
                floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,  // Red
                    0.349f, 0.686f, 0.168f, 0f, 0f,  // Green
                    0.272f, 0.534f, 0.131f, 0f, 0f,  // Blue
                    0.000f, 0.000f, 0.000f, 1f, 0f   // Alpha
                )
            )
        }
    }

    private fun getGrayScaleColorMatrix(): ColorMatrix = ColorMatrixUtils.saturateMatrix(0f)

    private fun getNoirColorMatrix(): ColorMatrix {
        val saturationMatrix = ColorMatrixUtils.saturateMatrix(0.0f)
        val contrastMatrix = ColorMatrixUtils.contrastMatrix(2.0f)
        return ColorMatrixUtils.concat(saturationMatrix, contrastMatrix)
    }

    private fun getInverseColorMatrix(): ColorMatrix {
        return ColorMatrix().apply {
            set(
                floatArrayOf(
                    -1.0f, 0.0f, 0.0f, 0f, 255f,  // Red
                    0.0f, -1.0f, 0.0f, 0f, 255f,  // Green
                    0.0f, 0.0f, -1.0f, 0f, 255f,  // Blue
                    0.0f, 0.0f, 0.0f, 1f, 0f   // Alpha
                )
            )
        }
    }

    private fun getWarmColorMatrix() = ColorMatrixUtils.warmMatrix(0.1f)

    private fun getColdColorMatrix() = ColorMatrixUtils.warmMatrix(-0.1f)

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