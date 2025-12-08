package com.truvideo.sdk.image.usecases

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

internal class BitmapResizeUseCase {
    val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(bitmap: Bitmap, width: Int? = null, height: Int? = null): Bitmap {
        return suspendCancellableCoroutine {
            scope.launch {
                val resizedBitmap = resize(bitmap, width, height)
                it.resumeWith(Result.success(resizedBitmap))
            }
        }
    }

    private fun resize(bitmap: Bitmap, width: Int?, height: Int?): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val newWidth: Int
        val newHeight: Int

        if (width == null && height == null) {
            return bitmap
        } else if (width == null) {
            newHeight = height!!
            newWidth = (newHeight * originalWidth) / originalHeight
        } else if (height == null) {
            newWidth = width
            newHeight = (newWidth * originalHeight) / originalWidth
        } else {
            newWidth = width
            newHeight = height
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}