package com.truvideo.sdk.image.app.ui.activities.main_activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.image.TruvideoSdkImage
import com.truvideo.sdk.image.app.ui.theme.TruvideoSdkImageTheme
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.ui.edit.activities.edit.TruvideoSdkImageEditContract
import com.truvideo.sdk.image.ui.edit.activities.edit.TruvideoSdkImageEditParams
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TruvideoSdkImageTheme {
                Content()
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content() {
        val scope = rememberCoroutineScope()
        var imageInfo by remember { mutableStateOf<TruvideoSdkImageInformation?>(null) }
        var editedImages by remember { mutableStateOf(persistentListOf<TruvideoSdkImageInformation>()) }

        val editImageLauncher = rememberLauncherForActivityResult(TruvideoSdkImageEditContract()) {
            if (it == null) return@rememberLauncherForActivityResult
            scope.launch {
                val info = try {
                    TruvideoSdkImage.getInfo(it)
                } catch (exception: Exception) {
                    null
                }
                if (info != null) {
                    editedImages = editedImages.toMutableList().apply { add(info) }.toPersistentList()
                }
            }
        }

//        val pickFileLauncher = rememberLauncherForActivityResult(TruvideoSdkFilePickerContract()) {
//            if (it == null) return@rememberLauncherForActivityResult
//            scope.launch {
//                val info = try {
//                    TruvideoSdkImage.getInfo(it)
//                } catch (exception: Exception) {
//                    exception.printStackTrace()
//                    null
//                }
//
//                if (info != null) {
//                    imageInfo = info
//                }
//            }
//        }

        fun deleteEditedPath(path: String) {
            try {
                File(path).delete()
                Log.d("TruvideoSdkImage", "Image $path deleted")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }


        Column {
            TopAppBar(title = { Text("Image module") })

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
//                            pickFileLauncher.launch(TruvideoSdkFilePickerType.Picture)
                        }
                    ) {
                        Text("Pick image")
                    }

                    val withImageInfo = remember(imageInfo) { imageInfo != null }
                    AnimatedContent(targetState = withImageInfo, label = "edit-button") { withImageInfoTarget ->
                        if (withImageInfoTarget) {
                            Button(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                                onClick = {
                                    editImageLauncher.launch(
                                        TruvideoSdkImageEditParams(
                                            inputPath = imageInfo!!.path,
                                            outputPath = ""
                                        )
                                    )
                                }
                            ) {
                                Text(text = "Edit image")
                            }
                        } else {
                            Box(Modifier.fillMaxWidth())
                        }
                    }

                }

                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(editedImages, key = { it.path }) { model ->
                            EditedImageListItem(
                                model = model,
                                deleteButtonVisible = true,
                                onButtonDeletePressed = {
                                    deleteEditedPath(model.path)
                                    editedImages = editedImages
                                        .toMutableList()
                                        .apply { removeIf { model.path == it.path } }
                                        .toPersistentList()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun Test() {
        TruvideoSdkImageTheme {
            Content()
        }
    }
}