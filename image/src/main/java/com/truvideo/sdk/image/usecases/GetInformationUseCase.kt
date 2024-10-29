package com.truvideo.sdk.image.usecases

import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.exceptions.TruvideoSdkException
import java.io.File
import kotlin.coroutines.suspendCoroutine


internal class GetInformationUseCase {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(
        imagePath: String
    ): TruvideoSdkImageInformation {
        return suspendCoroutine { cont ->
            scope.launch {
                try {
                    // Get the width and height of the image
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(imagePath, options)
                    var width = options.outWidth
                    var height = options.outHeight

                    // Get the orientation of the image
                    val exifInterface = ExifInterface(imagePath)
                    val exifOrientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
                    )

                    // You can use the width, height, and orientation values as needed
                    when (exifOrientation) {
                        ORIENTATION_ROTATE_270, ORIENTATION_ROTATE_90 -> {
                            val a = height
                            height = width
                            width = a
                        }
                    }

                    val info = TruvideoSdkImageInformation(
                        width = width,
                        height = height,
                        path = imagePath,
                        size = File(imagePath).length(),
                    )

                    cont.resumeWith(Result.success(info))
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
}