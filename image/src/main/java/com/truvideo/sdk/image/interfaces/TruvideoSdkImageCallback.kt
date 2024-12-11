package com.truvideo.sdk.image.interfaces

import truvideo.sdk.common.exceptions.TruvideoSdkException

interface TruvideoSdkImageCallback<T> {
    fun onComplete(result: T)

    fun onError(exception: TruvideoSdkException)
}