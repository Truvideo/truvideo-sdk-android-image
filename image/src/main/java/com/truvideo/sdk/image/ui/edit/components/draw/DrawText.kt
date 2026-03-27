package com.truvideo.sdk.image.ui.edit.components.draw

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truvideo.sdk.components.TruvideoColors
import com.truvideo.sdk.components.animated_content.TruvideoAnimatedContent
import com.truvideo.sdk.components.animated_fade_visibility.TruvideoAnimatedFadeVisibility
import com.truvideo.sdk.components.button.TruvideoButton
import com.truvideo.sdk.components.button.TruvideoIconButton
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton
import com.truvideo.sdk.image.ui.edit.components.crop.spToPx
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateColor
import com.truvideo.sdk.image.ui.edit.components.custom_animated.animateFloat
import com.truvideo.sdk.image.ui.edit.components.custom_animated.springAnimationFloatSpec
import com.truvideo.sdk.image.ui.edit.components.horizontal_slider.HorizontalSlider
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import com.truvideo.sdk.image.utils.ColorUtils
import com.truvideo.sdk.image.utils.DrawUtils
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun DrawTextDialog(
    dismiss: () -> Unit = {},
    containerSize: Size,
    create: (imageData: DrawingData) -> Unit = { _ -> },
) {
    val context = LocalContext.current

    BackHandler {
        dismiss()
    }

    Content(
        add = { text, style ->
            val textBitmap = DrawUtils.createTextImage(
                text = text,
                color = style.color,
                size = context.spToPx(style.fontSize.value),
                bold = style.fontWeight == FontWeight.Bold,
                italic = style.fontStyle == FontStyle.Italic
            )

            val model = DrawingData(
                id = System.currentTimeMillis().toString(),
                bitmap = textBitmap,
                position = Offset(
                    (1.0f - (textBitmap.width / containerSize.width)) * 0.5f,
                    (1.0f - (textBitmap.height / containerSize.height)) * 0.5f
                ),
                size = Size(
                    textBitmap.width / containerSize.width,
                    textBitmap.height / containerSize.height
                )
            )
            create(model)
        },
        close = { dismiss() }
    )
}

@Composable
private fun Content(
    add: (value: String, style: TextStyle) -> Unit = { _, _ -> },
    close: () -> Unit = {},
) {
    var text by remember { mutableStateOf("") }
    var upperCase by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(20f) }
    var bold by remember { mutableStateOf(false) }
    var italic by remember { mutableStateOf(false) }
    val colors = remember { ColorUtils.colors }
    var color by remember { mutableStateOf(Color.White) }
    val colorAnim = animateColor(color)
    val backgroundColor = if (color.luminance() < 0.5f) Color.White else Color.Black
    val backgroundColorAnim = animateColor(backgroundColor)
    val style = TextStyle(
        color = colorAnim,
        fontSize = textSize.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp)) {
                TruvideoIconButton(
                    icon = Icons.Outlined.Clear,
                    small = true,
                    onPressed = { close() },
                    identifier = "clear_btn",
                )
                Box(Modifier.weight(1f))
                TruvideoButton(
                    enabled = text.trim().isNotEmpty(),
                    leftIcon = Icons.Outlined.Check,
                    text = "Add",
                    identifier = "add_btn",
                    small = true,
                    fullWidth = false,
                    color = TruvideoColors.amber,
                    textColor = Color.Black,
                    onPressed = {
                        add(
                            if (upperCase) text.uppercase() else text,
                            style
                        )
                    }
                )
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {


                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColorAnim)
                            .padding(8.dp)
                    ) {

                        Box(Modifier.fillMaxWidth()) {
                            BasicTextField(
                                value = if (upperCase) text.uppercase() else text,
                                onValueChange = { text = it },
                                textStyle = style,
                                maxLines = 1,
                                cursorBrush = SolidColor(colorAnim),
                                modifier = Modifier.fillMaxWidth()
                            )

                            TruvideoAnimatedFadeVisibility(text.trim().isEmpty()) {
                                Text(if(upperCase) "Add some text...".uppercase() else "Add some text...",
                                    style = style,
                                    color = colorAnim.copy(colorAnim.alpha * 0.5f)
                                )
                            }
                        }
                    }

                    Box(Modifier.height(8.dp))

                    // Colors
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(8.dp)
                        ) {
                            colors.forEachIndexed { index, c ->
                                Box(Modifier.padding(start = if (index != 0) 4.dp else 0.dp)) {
                                    TruvideoScaleButton(
                                        onPressed = { color = c }
                                    ) {
                                        Box(
                                            Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(c)
                                                .border(2.dp, Color.White, CircleShape)
                                        ) {
                                            Box(Modifier.align(Alignment.Center)) {
                                                TruvideoAnimatedFadeVisibility(c == color) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Check,
                                                        contentDescription = "",
                                                        tint = if (c.luminance() > 0.5f) Color.Black else Color.White,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height((30 + 16).dp)
                                .width(8.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = persistentListOf(
                                            MaterialTheme.colorScheme.surfaceContainerHigh,
                                            MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.0f)
                                        )
                                    )
                                )
                        )

                        Box(
                            modifier = Modifier
                                .height((30 + 16).dp)
                                .width(8.dp)
                                .align(Alignment.CenterEnd)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = persistentListOf(
                                            MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.0f),
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                        )
                                    )
                                )
                        )
                    }


                    Box(Modifier.height(8.dp))


                    // Size
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(8.dp)
                    ){
                        HorizontalSlider(
                            value = textSize,
                            minValue = 10f,
                            maxValue = 30f,
                            onValueChange = {
                                textSize = it
                            }
                        ){
                            Box(
                                modifier = Modifier.size(30.dp),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    imageVector = Icons.Outlined.TextFields,
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier.size(it.dp)
                                )
                            }

                        }
                    }

                    Box(Modifier.height(8.dp))

                    // Uppercase
                    TruvideoScaleButton(
                        onPressed = { upperCase = !upperCase }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                "Uppercase",
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            TruvideoAnimatedContent(targetState = upperCase) {
                                Icon(
                                    imageVector = if (it) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Box(Modifier.height(8.dp))

                    // Bold
                    TruvideoScaleButton(
                        onPressed = { bold = !bold }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                "Bold",
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            TruvideoAnimatedContent(targetState = bold) {
                                Icon(
                                    imageVector = if (it) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Box(Modifier.height(8.dp))

                    // Italic
                    TruvideoScaleButton(
                        onPressed = { italic = !italic }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                "Italic",
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            TruvideoAnimatedContent(targetState = italic) {
                                Icon(
                                    imageVector = if (it) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruVideoSdkTheme {
        Column {
            Content()
        }
    }
}