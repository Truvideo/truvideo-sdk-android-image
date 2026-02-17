package com.truvideo.sdk.image.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.truvideo.sdk.image.ui.edit.activities.edit.TruvideoSdkImageEditContract
import com.truvideo.sdk.image.ui.edit.activities.edit.TruvideoSdkImageEditParams

class ExampleActivity : ComponentActivity() {
    private lateinit var launcher: ActivityResultLauncher<TruvideoSdkImageEditParams>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher = registerForActivityResult(TruvideoSdkImageEditContract()) { resultPath: String? ->
            // Handle result

        }
    }

    private fun editImage(path: String) {
        val outputPath = applicationContext.filesDir.path + "/result.png"
        launcher.launch(
            TruvideoSdkImageEditParams(
                inputPath = path,
                outputPath = outputPath
            )
        )
    }
}