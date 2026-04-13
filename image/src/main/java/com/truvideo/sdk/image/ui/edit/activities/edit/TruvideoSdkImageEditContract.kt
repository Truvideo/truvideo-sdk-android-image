package com.truvideo.sdk.image.ui.edit.activities.edit

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TruvideoSdkImageEditContract : ActivityResultContract<TruvideoSdkImageEditParams, String?>() {
    companion object {
        const val PARAMS = "params"
        const val OUTPUT_PATH = "output_path"
    }

    override fun createIntent(context: Context, input: TruvideoSdkImageEditParams): Intent {
        return Intent(context, TruvideoSdkImageEditActivity::class.java).apply {
            putExtra(PARAMS, input.toJson())
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode) {
            RESULT_OK -> {
                intent?.getStringExtra(OUTPUT_PATH)
            }

            else -> null
        }
    }
}

@Serializable
data class TruvideoSdkImageEditParams(
    val inputPath: String,
    val outputPath: String
) {
    fun toJson(): String = jsonConfig.encodeToString(this)

    companion object {

        private val jsonConfig = Json {
            ignoreUnknownKeys = true
        }

        fun fromJson(json: String): TruvideoSdkImageEditParams {
            return jsonConfig.decodeFromString(json)
        }
    }
}