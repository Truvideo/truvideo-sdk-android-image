package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.truvideo.sdk.image.exceptions.TruvideoSdkImageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

internal class GetBitmapUseCase {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(path: String): Bitmap {
        return suspendCoroutine { cont ->
            scope.launch {
                try {
                    val exif = ExifInterface(path)
                    val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val rotationInDegrees = exifToDegrees(rotation)

                    val bitmap = BitmapFactory.decodeFile(path)
                    val rotatedBitmap = if (rotationInDegrees != 0) {
                        val matrix = Matrix()
                        matrix.postRotate(rotationInDegrees.toFloat())
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    } else {
                        bitmap
                    }

                    cont.resumeWith(Result.success(rotatedBitmap))
                } catch (exception: Exception) {
                    exception.printStackTrace()

                    if (exception is TruvideoSdkImageException) {
                        cont.resumeWith(Result.failure(exception))
                    } else {
                        cont.resumeWith(Result.failure(TruvideoSdkImageException(exception.localizedMessage ?: "Unknown error")))
                    }
                }
            }
        }
    }

    private fun exifToDegrees(rotation: Int): Int {
        return when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

}