package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.truvideo.sdk.image.model.TruvideoSdkImageCropInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageOutputFormat
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.exceptions.TruvideoSdkException
import java.io.File
import kotlin.coroutines.suspendCoroutine

internal class EditImageUseCase(
    private val getBitmapUseCase: GetBitmapUseCase,
    private val saveBitmapUseCase: SaveBitmapUseCase
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun editBitmap(
        bitmap: Bitmap,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        newWidth: Int?,
        newHeight: Int?,
    ): Bitmap {
        return processBitmap(
            bitmap = bitmap,
            rotation = rotation,
            cropInformation = cropInformation,
            horizontalFlip = horizontalFlip,
            verticalFlip = verticalFlip,
            newWidth = newWidth,
            newHeight = newHeight,
        )
    }

    suspend operator fun invoke(
        imagePath: String,
        resultPath: String,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        newWidth: Int?,
        newHeight: Int?,
        outputFormat: TruvideoSdkImageOutputFormat,
        compressionQuality: Int
    ): String {
        if (!File(imagePath).exists()) {
            throw TruvideoSdkException("Image file not found")
        }

        val bitmap = getBitmapUseCase(imagePath)

        val editedBitmap = processBitmap(
            bitmap = bitmap,
            rotation = rotation,
            cropInformation = cropInformation,
            horizontalFlip = horizontalFlip,
            verticalFlip = verticalFlip,
            newWidth = newWidth,
            newHeight = newHeight
        )

        return saveBitmapUseCase(
            bitmap = editedBitmap,
            resultPath = resultPath,
            outputFormat = outputFormat,
            compressionQuality = compressionQuality
        )
    }

    private suspend fun processBitmap(
        bitmap: Bitmap,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        newWidth: Int?,
        newHeight: Int?
    ): Bitmap {
        Log.d("TruvideoSdkImage", "BitmapSize: ${bitmap.width}x${bitmap.height}")
        Log.d("TruvideoSdkImage", "Rotation: $rotation")
        Log.d("TruvideoSdkImage", "Crop: $cropInformation")
        Log.d("TruvideoSdkImage", "Horizontal Flip: $horizontalFlip")
        Log.d("TruvideoSdkImage", "VerticalFlip: $verticalFlip")

        return suspendCoroutine { cont ->
            scope.launch {
                try {

                    var result: Bitmap = bitmap

                    // horizontalFlip
                    if (horizontalFlip) {
                        result = flipBitmapHorizontal(result)
                    }

                    // verticalFlip
                    if (verticalFlip) {
                        result = flipBitmapVertical(result)
                    }

                    // Rotate
                    if (rotation != null) {
                        result = rotateBitmap(result, rotation)
                    }

                    // Crop
                    if (cropInformation != null) {
                        result = cropBitmap(result, cropInformation)
                    }

                    // Resize
                    if (newWidth != null || newHeight != null) {
                        val finalWidth: Int
                        val finalHeight: Int
                        if (newWidth == null && newHeight != null) {
                            finalWidth = bitmap.width * (bitmap.height / newHeight)
                            finalHeight = newHeight
                        } else if (newWidth != null && newHeight == null) {
                            finalWidth = newWidth
                            finalHeight = bitmap.height * (bitmap.width / newWidth)
                        } else {
                            finalWidth = bitmap.width
                            finalHeight = bitmap.height
                        }

                        result = resizeBitmap(
                            result,
                            finalWidth,
                            finalHeight
                        )
                    }

                    cont.resumeWith(Result.success(result))
                } catch (exception: Exception) {
                    exception.printStackTrace()

                    if (exception is TruvideoSdkException) {
                        cont.resumeWith(Result.failure(exception))
                    } else {
                        cont.resumeWith(Result.failure(TruvideoSdkException("Unknown error")))
                    }
                }
            }
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotation: TruvideoSdkImageRotation): Bitmap {
        val matrix = Matrix()
        when (rotation) {
            TruvideoSdkImageRotation.ROTATE_0 -> matrix.postRotate(0f)
            TruvideoSdkImageRotation.ROTATE_90 -> matrix.postRotate(90f)
            TruvideoSdkImageRotation.ROTATE_180 -> matrix.postRotate(180f)
            TruvideoSdkImageRotation.ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun cropBitmap(bitmap: Bitmap, cropInfo: TruvideoSdkImageCropInformation): Bitmap {
        return Bitmap.createBitmap(
            bitmap,
            (cropInfo.left * bitmap.width).toInt(),
            (cropInfo.top * bitmap.height).toInt(),
            (cropInfo.width * bitmap.width).toInt(),
            (cropInfo.height * bitmap.height).toInt()
        )
    }

    private fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun flipBitmapHorizontal(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }

    private fun flipBitmapVertical(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }
}