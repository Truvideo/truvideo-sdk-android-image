package com.truvideo.sdk.image.adapters

import com.truvideo.sdk.image.interfaces.TruvideoSdkImageAuthAdapter
import com.truvideo.sdk.image.interfaces.TruvideoSdkImageVersionPropertiesAdapter
import truvideo.sdk.common.exceptions.TruvideoSdkAuthenticationRequiredException
import truvideo.sdk.common.exceptions.TruvideoSdkNotInitializedException
import truvideo.sdk.common.sdk_common
import truvideo.sdk.common.util.TruvideoSdkCommonExceptionParser
import truvideo.sdk.common.util.parse

internal class TruvideoSdkImageAuthAdapterImpl(
    versionPropertiesAdapter: TruvideoSdkImageVersionPropertiesAdapter

) : TruvideoSdkImageAuthAdapter {

    private val validateAuthentication: Boolean = versionPropertiesAdapter.readProperty("validateAuthentication") != "false"

    private fun isAuthenticated(): Boolean {
        if (!validateAuthentication) return true

        try {
            return sdk_common.auth.isAuthenticated()
        } catch (exception: Exception) {
            val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
            parsedException.printStackTrace()
            throw parsedException
        }
    }

    private fun isInitialized(): Boolean {
        if (!validateAuthentication) return true

        try {
            return sdk_common.auth.isInitialized
        } catch (exception: Exception) {
            val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
            parsedException.printStackTrace()
            throw parsedException
        }
    }

    override fun validateAuthentication() {
        if (!validateAuthentication) return

        val isAuthenticated = isAuthenticated()
        if (!isAuthenticated) {
            throw TruvideoSdkAuthenticationRequiredException()
        }

        val isInitialized = isInitialized()
        if (!isInitialized) {
            throw TruvideoSdkNotInitializedException()
        }
    }
}