package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import com.truvideo.sdk.image.model.TruvideoSdkImageOutputFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.exceptions.TruvideoSdkException
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.suspendCoroutine

class SaveBitmapUseCase {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(
        bitmap: Bitmap,
        resultPath: String,
        outputFormat: TruvideoSdkImageOutputFormat,
        compressionQuality: Int
    ): String {
        return suspendCoroutine { cont ->
            scope.launch {
                try {
                    val file = File(resultPath)
                    val outStream = FileOutputStream(file)

                    when (outputFormat) {
                        TruvideoSdkImageOutputFormat.PNG -> bitmap.compress(
                            Bitmap.CompressFormat.PNG, compressionQuality, outStream
                        )

                        TruvideoSdkImageOutputFormat.JPG -> bitmap.compress(
                            Bitmap.CompressFormat.JPEG, compressionQuality, outStream
                        )
                    }
                    outStream.flush()
                    outStream.close()

                    cont.resumeWith(Result.success(resultPath))
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