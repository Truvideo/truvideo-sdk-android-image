package com.truvideo.sdk.image.ui.edit.components.crop

import android.content.Context
import android.util.TypedValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_fit.TruvideoAnimatedFit
import com.truvideo.sdk.image.ui.edit.components.crop.model.CropInformation
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateColor
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationColorSpec
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import com.truvideo.sdk.image.utils.CropUtils

@Composable
internal fun Crop(
    aspectRatio: Float = 1f,
    information: CropInformation,
    onInformationChange: ((dragging: Boolean, information: CropInformation) -> Unit) = { _, _ -> },
    enabled: Boolean = true
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val maxW = context.dpToPx(maxWidth.value)
        val maxH = context.dpToPx(maxHeight.value)

        val boxSize = remember(aspectRatio, maxW, maxH) {
            calculateDimensions(
                maxW = maxW,
                maxH = maxH,
                aspectRatio = aspectRatio
            )
        }

        val w = boxSize.width
        val wAnim = animateFloat(
            value = w,
            spec = springAnimationFloatSpec
        ).coerceAtLeast(0f)

        val h = boxSize.height
        val hAnim = animateFloat(
            value = h,
            spec = springAnimationFloatSpec
        ).coerceAtLeast(0f)

        val x = (maxW - boxSize.width) * 0.5f
        val xAnim = animateFloat(
            value = x,
            spec = springAnimationFloatSpec
        )

        val y = (maxH - boxSize.height) * 0.5f
        val yAnim = animateFloat(
            value = y,
            spec = springAnimationFloatSpec
        )

        if (wAnim > 0 && hAnim > 0) {
            Box(
                Modifier
                    .offset(
                        x = context.pxToDp(xAnim).dp,
                        y = context.pxToDp(yAnim).dp
                    )
                    .width(context.pxToDp(wAnim).dp)
                    .height(context.pxToDp(hAnim).dp)
            ) {
                Content(
                    size = Size(w, h),
                    information = information,
                    onInformationChange = onInformationChange,
                    enabled = enabled
                )
            }

        }

    }

}


@Composable
private fun Content(
    size: Size,
    information: CropInformation,
    onInformationChange: ((dragging: Boolean, information: CropInformation) -> Unit) = { _, _ -> },
    enabled: Boolean = true
) {
    val context = LocalContext.current

    val thumbSize = 40f
    val thumbBorderRadius = 4f
    val lineWidth = 2f
    var dragging by remember { mutableStateOf(false) }

    var topLeftPosition by remember {
        mutableStateOf(
            Offset(
                information.topLeft.x * size.width,
                information.topLeft.y * size.height
            )
        )
    }
    val topLeftXAnim = animateFloat(
        value = topLeftPosition.x,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )
    val topLeftYAnim = animateFloat(
        value = topLeftPosition.y,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )

    var topRightPosition by remember {
        mutableStateOf(
            Offset(
                information.bottomRight.x * size.width,
                information.topLeft.y * size.height
            )
        )
    }
    val topRightXAnim = animateFloat(
        value = topRightPosition.x,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )
    val topRightYAnim = animateFloat(
        value = topRightPosition.y,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )

    var bottomLeftPosition by remember {
        mutableStateOf(
            Offset(
                information.topLeft.x * size.width,
                information.bottomRight.y * size.height
            )
        )
    }
    val bottomLeftXAnim = animateFloat(
        value = bottomLeftPosition.x,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )
    val bottomLeftYAnim = animateFloat(
        value = bottomLeftPosition.y,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )

    var bottomRightPosition by remember {
        mutableStateOf(
            Offset(
                information.bottomRight.x * size.width,
                information.bottomRight.y * size.height
            )
        )
    }
    val bottomRightXAnim = animateFloat(
        value = bottomRightPosition.x,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )
    val bottomRightYAnim = animateFloat(
        value = bottomRightPosition.y,
        spec = springAnimationFloatSpec,
        animate = !dragging
    )

    LaunchedEffect(information, size) {
        if (dragging) return@LaunchedEffect
        topLeftPosition = Offset(
            information.topLeft.x * size.width,
            information.topLeft.y * size.height
        )
        topRightPosition = Offset(
            information.bottomRight.x * size.width,
            information.topLeft.y * size.height
        )
        bottomLeftPosition = Offset(
            information.topLeft.x * size.width,
            information.bottomRight.y * size.height
        )
        bottomRightPosition = Offset(
            information.bottomRight.x * size.width,
            information.bottomRight.y * size.height
        )
    }

    fun report(dragging: Boolean) {
        onInformationChange(
            dragging,
            CropInformation(
                topLeft = Offset(
                    topLeftPosition.x / size.width,
                    topLeftPosition.y / size.height
                ),
                bottomRight = Offset(
                    bottomRightPosition.x / size.width,
                    bottomRightPosition.y / size.height
                )
            )
        )
    }

    suspend fun PressGestureScope.onPointerPressed() {
        dragging = true
        val released = tryAwaitRelease()
        if (released) {
            dragging = false
        }
    }

    fun onDragEnd() {
        dragging = false
        report(false)
    }

    fun calculateColor(): Color {
        return if (enabled) {
            if (dragging) {
                TruvideoColors.amber
            } else {
                Color.White
            }
        } else {
            TruvideoColors.gray
        }
    }

    val colorAnim = animateColor(
        value = calculateColor(),
        spec = springAnimationColorSpec
    )

    Box(
        Modifier
            .fillMaxSize()
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()

                        canvas.saveLayer(size.toRect(), paint)

                        // Dim background
                        canvas.drawRect(
                            rect = size.toRect(),
                            paint = paint.apply {
                                this.color = Color.Black.copy(0.5f)
                                this.blendMode = BlendMode.SrcOver
                            }
                        )

                        // Clip dim background
                        canvas.drawRoundRect(
                            left = topLeftXAnim,
                            top = topLeftYAnim,
                            right = topRightXAnim,
                            bottom = bottomRightYAnim,
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = paint.apply {
                                this.color = colorAnim
                                this.blendMode = BlendMode.Clear
                            }
                        )

                        canvas.drawRoundRect(
                            left = topLeftXAnim,
                            top = topLeftYAnim,
                            right = topRightXAnim,
                            bottom = bottomRightYAnim,
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                            }
                        )

                        val d = context.dpToPx(lineWidth)

                        canvas.drawRoundRect(
                            left = topLeftXAnim + d,
                            top = topLeftYAnim + d,
                            right = topRightXAnim - d,
                            bottom = bottomRightYAnim - d,
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                                this.blendMode = BlendMode.Clear
                            }
                        )

                        canvas.restore()

                        val s = thumbSize * 0.5f

                        // Thumb top left
                        canvas.drawRoundRect(
                            left = topLeftXAnim,
                            top = topLeftYAnim,
                            right = topLeftXAnim + context.dpToPx(s),
                            bottom = topLeftYAnim + context.dpToPx(s),
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                            }
                        )

                        // Thumb top right
                        canvas.drawRoundRect(
                            left = topRightXAnim - context.dpToPx(s),
                            top = topRightYAnim,
                            right = topRightXAnim,
                            bottom = topRightYAnim + context.dpToPx(s),
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                            }
                        )

                        // Thumb bottom start
                        canvas.drawRoundRect(
                            left = bottomLeftXAnim,
                            top = bottomLeftYAnim - context.dpToPx(s),
                            right = bottomLeftXAnim + context.dpToPx(s),
                            bottom = bottomLeftYAnim,
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                            }
                        )

                        // Thumb bottom end
                        canvas.drawRoundRect(
                            left = bottomRightXAnim - context.dpToPx(s),
                            top = bottomRightYAnim - context.dpToPx(s),
                            right = bottomRightXAnim,
                            bottom = bottomRightYAnim,
                            radiusX = context.dpToPx(thumbBorderRadius),
                            radiusY = context.dpToPx(thumbBorderRadius),
                            paint = Paint().apply {
                                this.color = colorAnim
                            }
                        )
                    }
                }

        )

        // Center rectangle
        Box(
            modifier = Modifier
                .offset(
                    x = context.pxToDp(topLeftXAnim).dp,
                    y = context.pxToDp(topLeftYAnim).dp
                )
                .width(context.pxToDp(topRightXAnim - topLeftXAnim).dp)
                .height(context.pxToDp(bottomLeftYAnim - topLeftYAnim).dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = { onPointerPressed() })
                }
                .pointerInput(enabled, size) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        val currentW = topRightPosition.x - topLeftPosition.x
                        val currentH = bottomLeftPosition.y - topLeftPosition.y
                        val maxX = size.width - currentW
                        val maxY = size.height - currentH
                        val newX = (topLeftPosition.x + dragAmount.x).coerceIn(0f, maxX)
                        val newY = (topLeftPosition.y + dragAmount.y).coerceIn(0f, maxY)

                        topLeftPosition = Offset(newX, newY)
                        topRightPosition = Offset(newX + currentW, newY)
                        bottomLeftPosition = Offset(newX, newY + currentH)
                        bottomRightPosition = Offset(newX + currentW, newY + currentH)

                        change.consume()
                        report(true)
                    }
                }
        )

        // Top Left
        Box(
            Modifier
                .offset(
                    x = context.pxToDp(topLeftXAnim).dp,
                    y = context.pxToDp(topLeftYAnim).dp
                )
                .size(thumbSize.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = { onPointerPressed() })
                }
                .pointerInput(enabled, size) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        val newX = (topLeftPosition.x + dragAmount.x).coerceIn(
                            0f,
                            topRightPosition.x - context.dpToPx(thumbSize * 2)
                        )
                        val newY = (topLeftPosition.y + dragAmount.y).coerceIn(
                            0f,
                            bottomLeftPosition.y - context.dpToPx(thumbSize * 2)
                        )

                        topLeftPosition = Offset(newX, newY)
                        topRightPosition = Offset(topRightPosition.x, newY)
                        bottomLeftPosition = Offset(newX, bottomLeftPosition.y)

                        change.consume()
                        report(true)
                    }
                }
        )

        // Top Right
        Box(
            Modifier
                .offset(
                    y = context.pxToDp(topRightYAnim).dp,
                    x = context.pxToDp(topRightXAnim).dp - thumbSize.dp,
                )
                .size(thumbSize.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = { onPointerPressed() })
                }
                .pointerInput(enabled, size) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        val newX = (topRightPosition.x + dragAmount.x).coerceIn(
                            topLeftPosition.x + context.dpToPx(thumbSize * 2),
                            size.width
                        )
                        val newY = (topRightPosition.y + dragAmount.y).coerceIn(
                            0f,
                            bottomRightPosition.y - context.dpToPx(thumbSize * 2)
                        )

                        topLeftPosition = Offset(topLeftPosition.x, newY)
                        topRightPosition = Offset(newX, newY)
                        bottomRightPosition = Offset(newX, bottomRightPosition.y)

                        change.consume()
                        report(true)
                    }
                }
        )

        // Bottom Left
        Box(
            Modifier
                .offset(
                    x = context.pxToDp(bottomLeftXAnim).dp,
                    y = context.pxToDp(bottomLeftYAnim).dp - thumbSize.dp
                )
                .size(thumbSize.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = { onPointerPressed() })
                }
                .pointerInput(enabled, size) {
                    if (!enabled) return@pointerInput

                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        val newX = (bottomLeftPosition.x + dragAmount.x).coerceIn(
                            0f,
                            bottomRightPosition.x - context.dpToPx(thumbSize * 2)
                        )
                        val newY = (bottomLeftPosition.y + dragAmount.y).coerceIn(
                            topLeftPosition.y + context.dpToPx(thumbSize * 2),
                            size.height
                        )

                        topLeftPosition = Offset(newX, topLeftPosition.y)
                        bottomLeftPosition = Offset(newX, newY)
                        bottomRightPosition = Offset(bottomRightPosition.x, newY)

                        change.consume()
                        report(true)
                    }
                }
        )

        // Bottom Right
        Box(
            Modifier
                .offset(
                    x = context.pxToDp(bottomRightXAnim).dp - thumbSize.dp,
                    y = context.pxToDp(bottomRightYAnim).dp - thumbSize.dp
                )
                .size(thumbSize.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = { onPointerPressed() })
                }
                .pointerInput(enabled, size) {
                    if (!enabled) return@pointerInput

                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        val newX = (bottomRightPosition.x + dragAmount.x).coerceIn(
                            bottomLeftPosition.x + context.dpToPx(thumbSize * 2),
                            size.width
                        )
                        val newY = (bottomRightPosition.y + dragAmount.y).coerceIn(
                            topRightPosition.y + context.dpToPx(thumbSize * 2),
                            size.height
                        )

                        topRightPosition = Offset(newX, topRightPosition.y)
                        bottomLeftPosition = Offset(bottomLeftPosition.x, newY)
                        bottomRightPosition = Offset(newX, newY)

                        change.consume()
                        report(true)
                    }
                }
        )
    }
}

fun Context.dpToPx(dp: Float): Float {
    val density = resources.displayMetrics.density
    return dp * density
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun Context.pxToDp(px: Float): Float {
    val density = resources.displayMetrics.density
    return px / density
}

private fun calculateDimensions(maxW: Float, maxH: Float, aspectRatio: Float): Size {
    var w = maxW
    var h = maxW / aspectRatio

    if (h <= maxH) {
        return Size(w, h)
    }

    h = maxH
    w = maxH * aspectRatio

    return Size(w, h)
}


@Composable
@Preview(showBackground = true)
private fun Test() {
    val initialImageSize = Size(500f, 300f)
    val secondaryImageSize = Size(500f, 1500f)
    val initialCropInformation = CropInformation(
        topLeft = Offset(0.0f, 0.0f),
        bottomRight = Offset(0.5f, 0.5f),
    )
    var updates by remember { mutableIntStateOf(0) }
    var imageSize by remember { mutableStateOf(initialImageSize) }
    var cropInformation by remember { mutableStateOf(initialCropInformation) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var enabled by remember { mutableStateOf(true) }

    TruVideoSdkTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {

                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    Box(
                        Modifier.fillMaxSize()
                    ) {
                        TruvideoAnimatedFit(
                            rotation = rotation,
                            aspectRatio = imageSize.width / imageSize.height,
                            modifier = Modifier.fillMaxSize(),
                            contentModifier = Modifier
                                .background(Color.Red)
                                .fillMaxSize()
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Blue)
                            )

                            Crop(
                                enabled = enabled,
                                aspectRatio = imageSize.width / imageSize.height,
                                information = cropInformation,
                                onInformationChange = { _, info ->
                                    updates += 1
                                    cropInformation = info
                                }
                            )

                        }
                    }

                }

                Text(
                    "Enabled: $enabled",
                    modifier = Modifier.clickable {
                        enabled = !enabled
                    }
                )

                Text(
                    "Reset",
                    modifier = Modifier.clickable {
                        imageSize = initialImageSize
                        cropInformation = initialCropInformation
                        rotation = 0f
                    }
                )

                Text(
                    "Rotation: $rotation",
                    modifier = Modifier.clickable {
                        rotation = when (rotation) {
                            0f -> 90f
                            90f -> 180f
                            180f -> 270f
                            else -> 0f
                        }
                        cropInformation = CropUtils.rotate(cropInformation, 90f)
                    }
                )

                Text(
                    "Image Size: $imageSize",
                    modifier = Modifier.clickable {
                        imageSize = when (imageSize) {
                            initialImageSize -> secondaryImageSize
                            else -> initialImageSize
                        }
                    }
                )

                Text("Crop information:")
                Text("TL: (${cropInformation.topLeft.x}, ${cropInformation.topLeft.y})")
                Text("BR: (${cropInformation.bottomRight.x}, ${cropInformation.bottomRight.y})")
                Text("Width: ${cropInformation.width} -> ${cropInformation.calculateWidth(imageSize.width)}")
                Text("Height: ${cropInformation.height} -> ${cropInformation.calculateHeight(imageSize.height)}")
                Text("Updates: $updates")
            }
        }
    }
}