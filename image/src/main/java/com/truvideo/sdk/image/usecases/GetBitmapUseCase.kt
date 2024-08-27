package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.truvideo.sdk.image.exceptions.TruvideoSdkImageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class GetBitmapUseCase {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(path: String): Bitmap {
        return suspendCoroutine { cont ->
            scope.launch {
                try {
                    val bitmap = BitmapFactory.decodeFile(path)
                    cont.resumeWith(Result.success(bitmap))
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
}