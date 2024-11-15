package com.truvideo.sdk.image.app.ui.activities.main_activity

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.truvideo.sdk.components.animated_collapse_visibility.TruvideoAnimatedCollapseVisibility
import com.truvideo.sdk.components.button.TruvideoButton
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditedImageListItem(
    model: TruvideoSdkImageInformation,
    deleteButtonText: String = "Delete",
    deleteButtonVisible: Boolean = true,
    onButtonDeletePressed: () -> Unit = {}
) {
    var collapsed by remember { mutableStateOf(false) }
    val size = animateFloat(
        value = if(collapsed) 40f else 70f,
        spec = springAnimationFloatSpec
    )

    Card(Modifier.fillMaxWidth()) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    GlideImage(
                        model = Uri.fromFile(File(model.path)),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(size.dp)
                            .background(Color.White)
                    )
                    Box(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Path", style = MaterialTheme.typography.titleSmall)
                        Text(model.path, style = MaterialTheme.typography.bodySmall)

                        TruvideoAnimatedCollapseVisibility(!collapsed){
                            Column {
                                Box(Modifier.height(8.dp))

                                Text("Resolution", style = MaterialTheme.typography.titleSmall)
                                Text("${model.width}x${model.height}", style = MaterialTheme.typography.bodySmall)

                                Box(Modifier.height(8.dp))

                                Text("File size", style = MaterialTheme.typography.titleSmall)
                                Text("${model.size} bytes", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                TruvideoAnimatedCollapseVisibility(deleteButtonVisible && !collapsed) {
                    Column {
                        Box(Modifier.height(16.dp))
                        TruvideoButton(
                            text = deleteButtonText,
                            onPressed = { onButtonDeletePressed() }
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)) {
                TruvideoIconButton(
                    icon = if(collapsed) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                    color = Color.Transparent,
                    iconColor = Color.Black,
                    onPressed = { collapsed = !collapsed }
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    EditedImageListItem(
        model = TruvideoSdkImageInformation(
            path = "file path",
            width = 100,
            height = 100,
            size = 100
        )
    )
}