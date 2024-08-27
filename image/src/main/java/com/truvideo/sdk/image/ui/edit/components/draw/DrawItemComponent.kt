package com.truvideo.sdk.image.ui.edit.components.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_value.animateFloat
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.components.crop.pxToDp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateColor
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

@Composable
internal fun DrawItemComponent(
    enabled: Boolean = true,
    selected: Boolean = false,
    bitmap: Bitmap,
    imageSize: Size,
    contentPadding: Float = 10f
) {
    val context = LocalContext.current

    val lineColor = if (selected) TruvideoColors.amber else Color.White
    val lineColorAnim = animateColor(value = lineColor)

    val lineOpacity = if (enabled) 1f else 0f
    val lineOpacityAnim = animateFloat(
        value = lineOpacity,
        spec = springAnimationFloatSpec,
    ).coerceIn(0f, 1f)

    Box(
        modifier = Modifier.size(
            width = context.pxToDp(imageSize.width).dp + contentPadding.dp * 2,
            height = context.pxToDp(imageSize.height).dp + contentPadding.dp * 2
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(contentPadding.dp)
        ) {
            val b = remember(bitmap) { bitmap.asImageBitmap() }
            Image(
                bitmap = b,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val strokeWidth = context.dpToPx(2f)
                        val stepSize = context.dpToPx(10f)
                        val stepSeparator = context.dpToPx(4f)

                        val horizontalCount = (size.width / (stepSize + stepSeparator)).toInt()
                        val totalHorizontalSeparatorWidth = (horizontalCount - 1) * stepSeparator
                        val adjustedHorizontalStepSize =
                            (size.width - totalHorizontalSeparatorWidth - strokeWidth) / horizontalCount.toFloat()

                        val verticalCount = (size.height / (stepSize + stepSeparator)).toInt()
                        val totalVerticalSeparatorHeight = (verticalCount - 1) * stepSeparator
                        val adjustedVerticalStepSize =
                            (size.height - totalVerticalSeparatorHeight - strokeWidth) / verticalCount.toFloat()

                        val dashColor = lineColorAnim.copy(lineColorAnim.alpha * lineOpacityAnim)

                        // Border
                        canvas.drawRect(
                            left = strokeWidth,
                            top = strokeWidth,
                            right = size.width - strokeWidth,
                            bottom = size.height - strokeWidth,
                            paint = Paint().apply {
                                this.color = Color.Black.copy(0.5f * lineOpacityAnim)
                                this.style = PaintingStyle.Stroke
                                this.strokeWidth = strokeWidth * 2
                            }
                        )
//
                        // Top
                        for (i in 0 until horizontalCount) {
                            val x = i * (adjustedHorizontalStepSize + stepSeparator) + strokeWidth * 0.5f

                            canvas.drawLine(
                                p1 = Offset(x, strokeWidth),
                                p2 = Offset(x + adjustedHorizontalStepSize, strokeWidth),
                                paint = Paint().apply {
                                    this.color = dashColor
                                    this.strokeWidth = strokeWidth
                                }
                            )
                        }

                        // Bottom
                        for (i in 0 until horizontalCount) {
                            val x = i * (adjustedHorizontalStepSize + stepSeparator) + strokeWidth * 0.5f

                            canvas.drawLine(
                                p1 = Offset(x, size.height - strokeWidth),
                                p2 = Offset(x + adjustedHorizontalStepSize, size.height - strokeWidth),
                                paint = Paint().apply {
                                    this.color = dashColor
                                    this.strokeWidth = strokeWidth
                                }
                            )
                        }

                        // Left
                        for (i in 0 until verticalCount) {
                            val y = i * (adjustedVerticalStepSize + stepSeparator) + strokeWidth * 0.5f

                            canvas.drawLine(
                                p1 = Offset(strokeWidth, y),
                                p2 = Offset(strokeWidth, y + adjustedVerticalStepSize),
                                paint = Paint().apply {
                                    this.color = dashColor
                                    this.strokeWidth = strokeWidth
                                }
                            )
                        }

                        // Right
                        for (i in 0 until verticalCount) {
                            val y = i * (adjustedVerticalStepSize + stepSeparator) + strokeWidth * 0.5f

                            canvas.drawLine(
                                p1 = Offset(size.width - strokeWidth, y),
                                p2 = Offset(size.width - strokeWidth, y + adjustedVerticalStepSize),
                                paint = Paint().apply {
                                    this.color = dashColor
                                    this.strokeWidth = strokeWidth
                                }
                            )
                        }
                    }
                }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    var enabled by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color.White) }
    val bitmap = remember {
        val b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        canvas.drawColor(Color.Red.toArgb())
        b
    }


    TruVideoSdkTheme {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .weight(1f)
            ) {
                DrawItemComponent(
                    enabled = enabled,
                    selected = selected,
                    bitmap = bitmap,
                    imageSize = Size(500f, 500f)
                )
            }

            Text("Enabled: $enabled",
                modifier = Modifier.clickable {
                    enabled = !enabled
                }
            )

            Text("Selected: $selected",
                modifier = Modifier.clickable {
                    selected = !selected
                }
            )


            Text("Background color: $backgroundColor",
                modifier = Modifier.clickable {
                    backgroundColor = when (backgroundColor) {
                        Color.Black -> Color.White
                        else -> Color.Black
                    }
                }
            )
        }
    }
}