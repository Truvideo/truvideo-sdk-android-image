package com.truvideo.sdk.image

import android.graphics.Bitmap
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
import truvideo.sdk.common.exceptions.TruvideoSdkException

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
        width: Int?,
        height: Int?
    ): Bitmap {
        authAdapter.validateAuthentication()

        try {
            return editImageUseCase.editBitmap(
                bitmap,
                rotation,
                cropInformation,
                horizontalFlip,
                verticalFlip,
                width,
                height
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw exception as? TruvideoSdkException ?: TruvideoSdkException("Unknown error")
        }
    }

    override fun editBitmap(
        bitmap: Bitmap,
        rotation: TruvideoSdkImageRotation?,
        cropInformation: TruvideoSdkImageCropInformation?,
        horizontalFlip: Boolean,
        verticalFlip: Boolean,
        width: Int?,
        height: Int?,
        callback: TruvideoSdkImageCallback<Bitmap>
    ) {
        authAdapter.validateAuthentication()

        scope.launch {
            try {
                val result = editBitmap(
                    bitmap = bitmap,
                    rotation = rotation,
                    cropInformation = cropInformation,
                    horizontalFlip = horizontalFlip,
                    verticalFlip = verticalFlip,
                    width = width,
                    height = height,
                )
                callback.onComplete(result)
            } catch (exception: Exception) {
                if (exception is TruvideoSdkException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkException("Unknown error"))
                }
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
        width: Int?,
        height: Int?,
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
                width,
                height,
                outputFormat,
                compressionQuality
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException("Unknown error")
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
        width: Int?,
        height: Int?,
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
                    width,
                    height
                )
                callback.onComplete(resultPath)
            } catch (exception: Exception) {
                if (exception is TruvideoSdkException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkException("Unknown error"))
                }
            }
        }
    }

    override suspend fun getInfo(imagePath: String): TruvideoSdkImageInformation {
        authAdapter.validateAuthentication()

        try {
            return getImageInformationUseCase(imagePath)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException("Unknown error")
            }
        }
    }

    override fun getInfo(
        imagePath: String,
        callback: TruvideoSdkImageCallback<TruvideoSdkImageInformation>
    ) {
        scope.launch {
            try {
                val result = getInfo(imagePath)
                callback.onComplete(result)
            } catch (exception: Exception) {
                if (exception is TruvideoSdkException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkException("Unknown error"))
                }
            }
        }
    }

    override val environment: String = BuildConfig.FLAVOR

    override val version: String = BuildConfig.VERSION
}