package com.truvideo.sdk.image.ui.edit.components.custom_horizontal_pager


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton
import com.truvideo.sdk.image.model.ImageFilterType
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.components.crop.pxToDp
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.absoluteValue

@Composable
fun CustomHorizontalPager(
    count: Int,
    itemWidth: Float = 40f,
    itemHeight: Float = 40f,
    itemSpacing: Float = 4f,
    borderWidth: Float = 4f,
    value: Int = 0,
    onValueChanged: (Int) -> Unit = {},
    contentBuilder: @Composable (Int) -> Unit = {},
) {
    val context = LocalContext.current

    val itemWidthAnim = animateFloat(
        value = itemWidth,
        spec = springAnimationFloatSpec
    ).coerceAtLeast(0f)

    val itemHeightAnim = animateFloat(
        value = itemHeight,
        spec = springAnimationFloatSpec
    ).coerceAtLeast(0f)

    val itemSpacingAnim = animateFloat(
        value = itemSpacing,
        spec = springAnimationFloatSpec
    ).coerceAtLeast(0f)

    val borderWidthAnim = animateFloat(
        value = borderWidth,
        spec = springAnimationFloatSpec
    ).coerceAtLeast(0f)

    var offset by remember {
        mutableFloatStateOf(
            calculateItemX(
                index = value,
                spacing = context.dpToPx(itemSpacing),
                w = context.dpToPx(itemWidth)
            ) * -1
        )
    }
    var currentValue by remember { mutableIntStateOf(value) }

    var isDragging by remember { mutableStateOf(false) }
    val offsetAnim = animateFloat(
        value = offset,
        spec = springAnimationFloatSpec,
        animate = !isDragging
    )

    LaunchedEffect(value, count, itemWidth, itemSpacing) {
        if (isDragging) return@LaunchedEffect
        offset = calculateItemX(
            index = value,
            spacing = context.dpToPx(itemSpacing),
            w = context.dpToPx(itemWidth)
        ) * -1
        currentValue = value
    }


    fun changeValue(newValue: Int) {
        if (currentValue == newValue) {
            Log.d("TruvideoSdkImage", "Same value. $newValue")
            return
        }

        Log.d("TruvideoSdkImage", "Diff value. $currentValue $newValue")

        offset = calculateItemX(
            index = newValue,
            spacing = context.dpToPx(itemSpacing),
            w = context.dpToPx(itemWidth)
        ) * -1

        currentValue = newValue
        onValueChanged(newValue)
    }

    fun onDrag(dragAmount: Offset) {
        offset += dragAmount.x

        val newValue = calculatePosition(
            count = count,
            spacing = context.dpToPx(itemSpacing),
            w = context.dpToPx(itemWidth),
            offset = offset
        )

        if (currentValue != newValue) {
            currentValue = newValue
            onValueChanged(newValue)
        }
    }

    fun onDragEnd() {
        isDragging = false

        val newValue = calculatePosition(
            count = count,
            spacing = context.dpToPx(itemSpacing),
            w = context.dpToPx(itemWidth),
            offset = offset
        )

        offset = calculateItemX(
            index = newValue,
            spacing = context.dpToPx(itemSpacing),
            w = context.dpToPx(itemWidth)
        ) * -1

        changeValue(newValue)

//        if (value != newValue) {
//            currentValue = newValue
//            onValueChanged(newValue)
//        }
    }

    BoxWithConstraints {
        val containerW = context.dpToPx(maxWidth.value)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(itemWidth, itemSpacing) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, dragAmount ->
                        onDrag(dragAmount)
                        change.consume()
                    }
                }
                .padding(
                    top = borderWidthAnim.dp,
                    start = context.pxToDp(containerW * 0.5f - context.dpToPx(itemWidthAnim) * 0.5f).dp
                )

        ) {
            for (index in 0 until count) {
                key(index) {
                    val p = calculateItemX(
                        index = index,
                        spacing = context.dpToPx(itemSpacingAnim),
                        w = context.dpToPx(itemWidthAnim)
                    )

                    Box(
                        modifier = Modifier
                            .offset(
                                x = context.pxToDp(p + offsetAnim).dp
                            )
                    ) {
                        TruvideoScaleButton(
                            onPressed = { changeValue(index) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(itemWidthAnim.dp)
                                    .height(itemHeightAnim.dp)
                            ) {
                                contentBuilder(index)
                            }
                        }
                    }
                }
            }
        }

        Box(
            Modifier
                .width(itemWidthAnim.dp + (borderWidthAnim * 2).dp)
                .height(itemHeightAnim.dp + (borderWidthAnim * 2).dp)
                .border(borderWidthAnim.dp, Color.White, RoundedCornerShape(4.dp))
                .align(Alignment.Center)
        )
    }
}

private fun calculateItemX(index: Int, spacing: Float, w: Float): Float {
    val s = if (index == 0) {
        0f
    } else {
        (index) * spacing
    }

    return (index * w) + s
}

private fun calculatePosition(
    count: Int,
    spacing: Float,
    w: Float,
    offset: Float
): Int {
    for (index in count - 1 downTo 0) {
        val p = calculateItemX(
            index = index,
            spacing = spacing,
            w = w
        )

        if (offset < 0) {
            if (offset.absoluteValue >= (p - w * 0.5f)) {
                return index
            }
        }
    }

    return 0
}


@Composable
@Preview(showBackground = true)
private fun Test() {
    val items = remember { ImageFilterType.entries.toPersistentList() }
    var itemWidth by remember { mutableFloatStateOf(40f) }
    var itemHeight by remember { mutableFloatStateOf(40f) }
    var borderWidth by remember { mutableStateOf(4f) }
    var itemSpacing by remember { mutableStateOf(4f) }
    var selected by remember { mutableStateOf(items.first()) }

    Column {
        Box(Modifier.background(Color.Black)) {
            CustomHorizontalPager(
                itemWidth = itemWidth,
                itemHeight = itemHeight,
                borderWidth = borderWidth,
                itemSpacing = itemSpacing,
                value = selected.ordinal,
                onValueChanged = { selected = items[it] },
                count = items.size,
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }
        }

        Text("Selected: ${selected.name}")

        Text(
            "W: $itemWidth",
            modifier = Modifier.clickable {
                itemWidth = when (itemWidth) {
                    30f -> 40f
                    40f -> 50f
                    50f -> 60f
                    60f -> 30f
                    else -> 40f
                }
            }
        )

        Text(
            "H: $itemHeight",
            modifier = Modifier.clickable {
                itemHeight = when (itemHeight) {
                    30f -> 40f
                    40f -> 50f
                    50f -> 60f
                    60f -> 30f
                    else -> 40f
                }
            }
        )

        Text("Border Width: $borderWidth",
            modifier = Modifier.clickable {
                borderWidth = when (borderWidth) {
                    4f -> 6f
                    6f -> 8f
                    8f -> 4f
                    else -> 4f
                }
            }
        )

        Text("Item spacing: $itemSpacing",
            modifier = Modifier.clickable {
                itemSpacing = when (itemSpacing) {
                    4f -> 8f
                    8f -> 12f
                    12f -> 4f
                    else -> 4f
                }
            }
        )

        Text(
            "Reset",
            modifier = Modifier.clickable {
                selected = items.first()
            }
        )
    }
}