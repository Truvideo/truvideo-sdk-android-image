package com.truvideo.sdk.image.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class TruvideoSdkImageInformation(
    val path: String,
    val width: Int,
    val height: Int,
    val size: Long
) {
    val aspectRatio: Float
        get() {
            if (height == 0) return 1f
            return width.toFloat() / height
        }

    fun toJson(): String = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String): TruvideoSdkImageInformation {
            val jsonConfig = Json {
                ignoreUnknownKeys = true
            }
            return jsonConfig.decodeFromString(json)
        }
    }
}