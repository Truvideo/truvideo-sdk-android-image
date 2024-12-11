package com.truvideo.sdk.image.interfaces

internal interface TruvideoSdkImageVersionPropertiesAdapter {
    fun readProperty(propertyName: String): String?
}