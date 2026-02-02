package com.truvideo.sdk.image.interfaces

import truvideo.sdk.common.exceptions.TruvideoSdkException


interface TruvideoSdkImageEditCallback {
    fun onReady(resultPath: String?)

    fun onError(exception: TruvideoSdkException)
}