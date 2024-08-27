package com.truvideo.sdk.image.interfaces

import truvideo.sdk.common.exception.TruvideoSdkException

interface TruvideoSdkImageEditCallback {
    fun onReady(resultPath: String?)

    fun onError(exception: TruvideoSdkException)
}