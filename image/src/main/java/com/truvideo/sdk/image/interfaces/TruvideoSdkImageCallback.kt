package com.truvideo.sdk.image.interfaces

import com.truvideo.sdk.image.exceptions.TruvideoSdkImageException

interface TruvideoSdkImageCallback<T> {
    fun onComplete(result: T)

    fun onError(exception: TruvideoSdkImageException)
}