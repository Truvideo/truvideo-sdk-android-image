package com.truvideo.sdk.image.ui.edit.components.preview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_flip.FlipDirection
import com.truvideo.sdk.components.animated_flip.TruvideoAnimatedFlip
import com.truvideo.sdk.image.ui.edit.components.animated_fit.AnimatedRotatedAspectRatio
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import android.graphics.Color as CanvasColor

@Composable
internal fun PicturePreview(
    bitmap: Bitmap?,
    rotation: Float = 0f,
    aspectRatio: Float = 1.0f,
    horizontalFlip: Boolean = false,
    verticalFlip: Boolean = false,
    animate: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedRotatedAspectRatio(
            aspectRatio = aspectRatio,
            rotation = rotation,
            animate = animate
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
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AnimatedContent(
                            targetState = bitmap,
                            label = "bitmap-preview",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { bitmapTarget ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (bitmapTarget != null) {
                                    val bitmapImage = remember(bitmapTarget) { bitmapTarget.asImageBitmap() }
                                    Image(
                                        bitmap = bitmapImage,
                                        contentDescription = "",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(TruvideoColors.gray)
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize())
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
        }
    }
}

private fun createBitmap(w: Int, h: Int): Bitmap {
    val result = Bitmap.createBitmap(
        w,
        h,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(result)
    val paint = Paint()
    paint.color = CanvasColor.RED
    canvas.drawRect(
        0f,
        0f,
        result.width.toFloat(),
        result.height.toFloat(),
        paint
    )

    return result
}


@Composable
@Preview
private fun Test() {
    val bitmap = remember { createBitmap(2000, 400) }

    TruVideoSdkTheme {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PicturePreview(
                    bitmap = bitmap,
                    aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                )
            }
        }
    }
}