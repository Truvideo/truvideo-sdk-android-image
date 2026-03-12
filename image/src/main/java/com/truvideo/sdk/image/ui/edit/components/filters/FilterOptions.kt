package com.truvideo.sdk.image.ui.edit.components.filters

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_content.TruvideoAnimatedContent
import com.truvideo.sdk.components.animated_opacity.TruvideoAnimatedOpacity
import com.truvideo.sdk.components.animated_scale.TruvideoAnimatedScale
import com.truvideo.sdk.image.model.ImageFilterType
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.components.custom_gradient.horizontalGradient
import com.truvideo.sdk.image.ui.edit.components.custom_horizontal_pager.CustomHorizontalPager
import com.truvideo.sdk.image.ui.edit.components.horizontal_slider.scaleFloat
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import com.truvideo.sdk.image.usecases.BitmapFilterUseCase
import com.truvideo.sdk.image.usecases.BitmapResizeUseCase
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

private val bitmapResizeUseCase = BitmapResizeUseCase()
private val bitmapFilterUseCase = BitmapFilterUseCase()

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilterOptions(
    bitmap: Bitmap? = null,
    type: ImageFilterType,
    intensity: Float = 1f,
    onIntensityChange: (Float) -> Unit = {},
    onTypeChange: (ImageFilterType) -> Unit = {}
) {
    val items = remember { ImageFilterType.entries.toPersistentList() }
    var thumbnails by remember { mutableStateOf<ImmutableMap<Int, Bitmap>>(persistentMapOf()) }

    LaunchedEffect(bitmap, items) {
        if (bitmap == null) {
            thumbnails = persistentMapOf()
        } else {
            items.forEachIndexed { index, type ->
                val resized = bitmapResizeUseCase(bitmap, 100)
                val filtered = bitmapFilterUseCase(resized, type)
                thumbnails = thumbnails.toMutableMap().apply { put(index, filtered) }.toPersistentMap()
            }
        }
    }

    Column {

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clipToBounds()
        ) {
            CustomHorizontalPager(
                count = items.size,
                value = type.ordinal,
                itemHeight = 40f,
                itemWidth = 40f,
                borderWidth = 4f,
                onValueChanged = {
                    val newType = ImageFilterType.entries[it]
                    Log.d("TruvideoSdkImage", "onValueChanged: $it. NewType: ${newType.name}")
                    onTypeChange(newType)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TruvideoColors.gray)
                ) {
                    val thumbnail = thumbnails[it]

                    TruvideoAnimatedContent(targetState = thumbnail) { thumbnailTarget ->
                        if (thumbnailTarget != null) {
                            val imageBitmap = thumbnailTarget.asImageBitmap()
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(Modifier.fillMaxSize())
                        }
                    }
                }
            }

            Box(
                Modifier
                    .width(16.dp)
                    .height((40 + 8).dp)
                    .horizontalGradient(
                        persistentListOf(
                            Color.Black.copy(1.0f),
                            Color.Black.copy(0.0f)
                        )
                    )
            )

            Box(
                Modifier
                    .width(16.dp)
                    .height((40 + 8).dp)
                    .horizontalGradient(
                        persistentListOf(
                            Color.Black.copy(0.0f),
                            Color.Black.copy(1.0f)
                        )
                    )
                    .align(Alignment.CenterEnd)
            )
        }

        val sliderVisible = remember(type) { type != ImageFilterType.Default }
        AnimatedContent(targetState = sliderVisible, label = "slider-visibility") { visible ->
            if (visible) {
                FilterSlider(
                    value = intensity,
                    onChanged = onIntensityChange
                )
            } else {
                Box(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun FilterSlider(
    value: Float = 1f,
    onChanged: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val shadowColor = Color.Black
    var dragging by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val w = context.dpToPx(maxWidth.value)

            var offset by remember { mutableFloatStateOf(value * w) }
            val offsetAnim = animateFloat(
                value = offset,
                spec = springAnimationFloatSpec,
                animate = !dragging
            )

            LaunchedEffect(value) {
                if (dragging) return@LaunchedEffect
                offset = value * w
            }

            fun onDragEnd() {
                val p = (offset / w).coerceIn(0.0f, 1.0f)
                offset = p * w
                dragging = false
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier) {
                    TruvideoAnimatedOpacity(opacity = if (dragging) 1f else 0f) {
                        TruvideoAnimatedScale(scale = if (dragging) 1f else 0.4f) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TruvideoColors.amber)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .animateContentSize { _, _ -> }
                            ) {
                                val p = (offsetAnim / w).coerceIn(0.0f, 1.0f)
                                val textValue = (p * 100).toInt()
                                Text(
                                    "$textValue",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                    }
                }
                Box(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .pointerInput(w) {
                            detectDragGestures(
                                onDragStart = { dragging = true },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            ) { change, dragAmount ->
                                offset -= dragAmount.x
                                val p = (offset / w).coerceIn(0.0f, 1.0f)
                                onChanged(p)
                                change.consume()
                            }
                        }
                        .drawBehind {
                            val p = (offsetAnim / w)
                            val translation = scaleFloat(
                                value = p,
                                fromMin = 0f,
                                fromMax = 1f,
                                toMin = size.width * 0.5f,
                                toMax = -size.width * 0.5f
                            )

//                            val translation = offsetAnim - size.width*0.5f

                            drawIntoCanvas { canvas ->

                                val thinLineWidth = context.dpToPx(1f)
                                val thinLineHeight = size.height * 0.8f
                                val thickLineWidth = context.dpToPx(2f)
                                val thickLineHeight = size.height * 1.0f

                                // Thick lines
                                for (i in 0..4) {
                                    val x = i * (size.width / 4) + translation
                                    canvas.drawLine(
                                        p1 = Offset(x, size.height - thickLineHeight + thickLineWidth * 0.5f),
                                        p2 = Offset(x, size.height - thickLineWidth * 0.5f),
                                        paint = Paint().apply {
                                            this.color = Color.White
                                            this.strokeWidth = thickLineWidth
                                            this.strokeCap = StrokeCap.Round
                                        }
                                    )
                                }

                                // Thin lines
                                for (i in 0..4 * 10) {
                                    val x = i * (size.width / (4 * 10)) + translation
                                    canvas.drawLine(
                                        p1 = Offset(x, size.height - thinLineHeight + thinLineWidth * 0.5f),
                                        p2 = Offset(x, size.height - thinLineWidth * 0.5f),
                                        paint = Paint().apply {
                                            this.color = Color.White
                                            this.strokeWidth = thinLineWidth
                                            this.strokeCap = StrokeCap.Round
                                        }
                                    )
                                }


                                // Center line
                                val centerLineWidth = context.dpToPx(4f)
                                val centerLineX = size.width * 0.5f
                                canvas.drawLine(
                                    p1 = Offset(centerLineX, centerLineWidth * 0.5f),
                                    p2 = Offset(centerLineX, size.height - centerLineWidth * 0.5f),
                                    paint = Paint().apply {
                                        this.color = TruvideoColors.amber
                                        this.strokeWidth = centerLineWidth
                                        this.strokeCap = StrokeCap.Round
                                    }
                                )
                            }
                        }
                )
            }

        }


        Box(
            Modifier
                .height((30 + 16 + 16).dp)
                .align(Alignment.BottomStart)
        ) {
            Row {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .background(shadowColor)
                )
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = persistentListOf(
                                    shadowColor,
                                    shadowColor.copy(0.0f)
                                )
                            )
                        )
                )
            }
        }

        Box(
            Modifier
                .height((30 + 16 + 16).dp)
                .align(Alignment.BottomEnd)
        ) {
            Row {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .horizontalGradient(
                            persistentListOf(
                                shadowColor.copy(0.0f),
                                shadowColor
                            )
                        )
                )

                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .background(shadowColor)
                )

            }
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var type by remember { mutableStateOf(ImageFilterType.Cold) }
    var intensity by remember { mutableFloatStateOf(0.0f) }
    TruVideoSdkTheme {
        Box(Modifier.background(Color.Black)) {

            FilterOptions(
                type = type,
                onTypeChange = { type = it },
                intensity = intensity,
                onIntensityChange = { intensity = it }
            )
        }
    }
}