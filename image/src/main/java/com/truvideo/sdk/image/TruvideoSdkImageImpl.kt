package com.truvideo.sdk.image

import android.graphics.Bitmap
import com.truvideo.sdk.image.exceptions.TruvideoSdkImageException
import com.truvideo.sdk.image.interfaces.TruvideoSdkImage
import com.truvideo.sdk.image.interfaces.TruvideoSdkImageAuthAdapter
import com.truvideo.sdk.image.interfaces.TruvideoSdkImageCallback
import com.truvideo.sdk.image.model.TruvideoSdkImageCropInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageOutputFormat
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation
import com.truvideo.sdk.image.usecases.EditImageUseCase
import com.truvideo.sdk.image.usecases.GetInformationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class TruvideoSdkImageImpl(
    private val authAdapter: TruvideoSdkImageAuthAdapter,
    private val getImageInformationUseCase: GetInformationUseCase,
    private val editImageUseCase: EditImageUseCase
) : TruvideoSdkImage {

    private val scope = CoroutineScope(Dispatchers.Main)

    override suspend fun editBitmap(
        bitmap: Bitmap,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        newWidth: Int?,
        newHeight: Int?
    ): Bitmap {
        authAdapter.validateAuthentication()

        try {
            return editImageUseCase.editBitmap(
                bitmap,
                rotation,
                cropInformation,
                horizontalFlip,
                verticalFlip,
                newWidth,
                newHeight
            )
        } catch (exception: Exception) {
            exception.printStackTrace()

            if (exception is TruvideoSdkImageException) {
                throw exception
            } else {
                throw TruvideoSdkImageException(exception.message ?: "")
            }
        }
    }

    override suspend fun edit(
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
    ) {
        authAdapter.validateAuthentication()

        try {
            editImageUseCase(
                imagePath,
                resultPath,
                rotation,
                cropInformation,
                horizontalFlip,
                verticalFlip,
                newWidth,
                newHeight,
                outputFormat,
                compressionQuality
            )
        } catch (exception: Exception) {
            exception.printStackTrace()

            if (exception is TruvideoSdkImageException) {
                throw exception
            } else {
                throw TruvideoSdkImageException(exception.message ?: "")
            }
        }
    }

    override fun edit(
        imagePath: String,
        resultPath: String,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        newWidth: Int?,
        newHeight: Int?,
        outputFormat: TruvideoSdkImageOutputFormat,
        compressionQuality: Int,
        callback: TruvideoSdkImageCallback<String>
    ) {
        scope.launch {
            try {
                edit(
                    imagePath,
                    resultPath,
                    rotation,
                    cropInformation,
                    horizontalFlip,
                    verticalFlip,
                    newWidth,
                    newHeight
                )
                callback.onComplete(resultPath)
            } catch (exception: Exception) {
                exception.printStackTrace()

                if (exception is TruvideoSdkImageException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkImageException(exception.localizedMessage ?: "Unknown error"))
                }
            }
        }
    }

    override suspend fun getInfo(imagePath: String): TruvideoSdkImageInformation {
        authAdapter.validateAuthentication()
        return getImageInformationUseCase(imagePath)
    }

    override fun getInfo(imagePath: String, callback: TruvideoSdkImageCallback<TruvideoSdkImageInformation>) {
        scope.launch {
            try {
                val result = getInfo(imagePath)
                callback.onComplete(result)
            } catch (exception: Exception) {
                exception.printStackTrace()

                if (exception is TruvideoSdkImageException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkImageException(exception.localizedMessage ?: "Unknown error"))
                }
            }
        }
    }
}