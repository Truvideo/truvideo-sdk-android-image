package com.truvideo.sdk.image.interfaces

import android.graphics.Bitmap
import com.truvideo.sdk.image.model.TruvideoSdkImageCropInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageOutputFormat
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation

interface TruvideoSdkImage {
    suspend fun editBitmap(
        bitmap: Bitmap,
        rotation: TruvideoSdkImageRotation? = null,
        cropInformation: TruvideoSdkImageCropInformation? = null,
        horizontalFlip: Boolean = false,
        verticalFlip: Boolean = false,
        newWidth: Int? = null,
        newHeight: Int? = null,
    ): Bitmap

    suspend fun edit(
        imagePath: String,
        resultPath: String,
        rotation: TruvideoSdkImageRotation? = null,
        cropInformation: TruvideoSdkImageCropInformation? = null,
        horizontalFlip: Boolean = false,
        verticalFlip: Boolean = false,
        newWidth: Int? = null,
        newHeight: Int? = null,
        outputFormat: TruvideoSdkImageOutputFormat = TruvideoSdkImageOutputFormat.JPG,
        compressionQuality: Int = 100
    )

    fun edit(
        imagePath: String,
        resultPath: String,
        rotation: TruvideoSdkImageRotation? = null,
        cropInformation: TruvideoSdkImageCropInformation? = null,
        horizontalFlip: Boolean = false,
        verticalFlip: Boolean = false,
        newWidth: Int? = null,
        newHeight: Int? = null,
        outputFormat: TruvideoSdkImageOutputFormat = TruvideoSdkImageOutputFormat.JPG,
        compressionQuality: Int = 100,
        callback: TruvideoSdkImageCallback<String>
    )

    suspend fun getInfo(imagePath: String): TruvideoSdkImageInformation

    fun getInfo(imagePath: String, callback: TruvideoSdkImageCallback<TruvideoSdkImageInformation>)
}