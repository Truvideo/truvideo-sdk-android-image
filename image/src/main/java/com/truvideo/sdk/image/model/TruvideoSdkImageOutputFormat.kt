package com.truvideo.sdk.image.model

enum class TruvideoSdkImageOutputFormat {
    PNG,
    JPG
}

val TruvideoSdkImageOutputFormat.extension: String
    get() {
        return when (this) {
            TruvideoSdkImageOutputFormat.PNG -> "png"
            TruvideoSdkImageOutputFormat.JPG -> "jpg"
        }
    }