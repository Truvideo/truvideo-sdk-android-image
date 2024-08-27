package com.truvideo.sdk.image.ui.edit.components.preview

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_fit.TruvideoAnimatedFit
import com.truvideo.sdk.components.animated_flip.FlipDirection
import com.truvideo.sdk.components.animated_flip.TruvideoAnimatedFlip

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Preview(
    bitmap: Bitmap?,
    rotation: Float = 0f,
    aspectRatio: Float = 1.0f,
    horizontalFlip: Boolean = false,
    verticalFlip: Boolean = false,
    previewPadding: PaddingValues = PaddingValues(),
    animate: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    Box(Modifier.fillMaxSize()) {
        TruvideoAnimatedFit(
            aspectRatio = aspectRatio,
            rotation = rotation,
            animate = animate,
            modifier = Modifier.fillMaxSize(),
            contentModifier = Modifier.align(Alignment.Center),




        ) {
            TruvideoAnimatedFlip(
                direction = FlipDirection.Horizontal,
                flipped = horizontalFlip,
                animate = animate
            ) {
                TruvideoAnimatedFlip(
                    direction = FlipDirection.Vertical,
                    flipped = verticalFlip,
                    animate = animate
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = bitmap,
                            label = "bitmap-preview",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { bitmapTarget ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                if (bitmapTarget != null) {

                                    val bitmapImage = remember(bitmapTarget) { bitmapTarget.asImageBitmap() }

                                    Image(
                                        bitmap = bitmapImage,
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize()
                                            .padding(previewPadding)
                                            .background(TruvideoColors.gray)
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize())
                                }
                            }
                        }
                        content()
                    }
                }
            }
        }
    }
}


@Composable
@Preview
private fun Test() {
//    var rotation by remember { mutableFloatStateOf(0f) }
//    var flippedHorizontal by remember { mutableStateOf(false) }
//    var flippedVertical by remember { mutableStateOf(false) }
//    var cropInformation by remember { mutableStateOf(CropInformation.zero()) }
//    val imageSize = IntSize(500, 900)
//    val aspectRatio = 1.5f
//    var cropAspectRatio by remember { mutableFloatStateOf(1f) }
//
//    LaunchedEffect(rotation) {
//        cropAspectRatio = if (rotation == 0f || rotation == 180f) {
//            aspectRatio
//        } else {
//            1 / aspectRatio
//        }
//    }
//
//
//    Box(Modifier.fillMaxSize()) {
//
//        Column {
//            Box(
//                Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//            ) {
//                Preview(
//                    bitmap = null,
//                    rotation = rotation,
//                    aspectRatio = aspectRatio,
//                    flippedHorizontal = flippedHorizontal,
//                    flippedVertical = flippedVertical,
//                )
//
//                Crop(
//                    information = cropInformation,
//                    imageSize = imageSize,
//                    onInformationChange = { cropInformation = it }
//                )
//            }
//
//            Text(
//                "Rotation: $rotation",
//                Modifier.clickable {
//                    rotation += 90f
//                }
//            )
//
//            Text(
//                "Reset rotation",
//                Modifier.clickable {
//                    rotation = 0f
//                }
//            )
//
//            Text(
//                "Flipped Horizontal: $flippedHorizontal",
//                Modifier.clickable {
//                    flippedHorizontal = !flippedHorizontal
//                }
//            )
//
//            Text(
//                "Flipped Vertical: $flippedVertical",
//                Modifier.clickable {
//                    flippedVertical = !flippedVertical
//                }
//            )
//        }
//    }

}