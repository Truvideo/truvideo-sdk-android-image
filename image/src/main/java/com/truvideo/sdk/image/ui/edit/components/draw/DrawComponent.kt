package com.truvideo.sdk.image.ui.edit.components.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.sdk.components.animated_collapse_visibility.TruvideoAnimatedCollapseVisibility
import com.truvideo.sdk.components.draggable_container.TruvideoDraggableContainer
import com.truvideo.sdk.components.scale_button.TruvideoScaleButton
import com.truvideo.sdk.image.ui.edit.activities.edit.model.DrawMode
import com.truvideo.sdk.image.ui.edit.components.crop.dpToPx
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme
import com.truvideo.sdk.image.utils.DrawUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun DrawComponent(
    animate: Boolean = false,
    drawMode: DrawMode? = DrawMode.Pencil,
    drawingColor: Color = Color.Black,
    selectedId: String = "",
    drawingWidth: Float = 4f,
    images: ImmutableList<DrawingData> = persistentListOf(),
    onImagePressed: (DrawingData) -> Unit = {},
    changeImage: (DrawingData) -> Unit = {},
    addImage: (DrawingData) -> Unit = {}
) {
    val context = LocalContext.current
    var tempDrawing by remember { mutableStateOf<DrawingPencilData?>(null) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth.value
        val h = maxHeight.value
        val containerSize = remember(w, h) {
            Size(
                context.dpToPx(w),
                context.dpToPx(h)
            )
        }
        val pointerInputKeys = remember(containerSize, drawMode) { persistentListOf(containerSize, drawMode?.name ?: "") }

        fun addDrawing() {
            val drawing = tempDrawing ?: return
            addImage(
                generateImage(
                    context = context,
                    data = drawing,
                    containerSize = containerSize
                )
            )
        }

        fun onDragStart(position: Offset) {
            val points = persistentListOf(
                Offset(
                    position.x / containerSize.width,
                    position.y / containerSize.height
                )
            )

            tempDrawing = DrawingPencilData(
                id = System.currentTimeMillis().toString(),
                points = points,
                color = drawingColor,
                strokeWidth = drawingWidth
            )
        }

        fun onDrag(position: Offset) {
            val drawing = tempDrawing ?: return

            val x = position.x / containerSize.width
            val y = position.y / containerSize.height
            val newPoints = drawing.points.toMutableList().apply { add(Offset(x, y)) }.toPersistentList()

            tempDrawing = drawing.copy(points = newPoints)
        }

        fun onDragCancel() {
            addDrawing()
            tempDrawing = null
        }

        val tempDrawingPath = remember(tempDrawing?.points, containerSize) {
            if (tempDrawing == null || tempDrawing!!.points.isEmpty()) {
                null
            } else {
                val result = Path()
                tempDrawing!!.points.forEachIndexed { index, offset ->
                    val x = offset.x * containerSize.width
                    val y = offset.y * containerSize.height
                    if (index == 0) {
                        result.moveTo(x, y)
                    } else {
                        result.lineTo(x, y)
                    }
                }
                result
            }
        }


        Box(modifier = Modifier.fillMaxSize()) {


            // Images
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.wrapContentSize(
                        unbounded = true,
                        align = Alignment.TopStart
                    )
                ) {
                    images.forEach { image ->
                        key(image.id) {
                            Box {
                                val itemW = image.size.width * containerSize.width
                                val itemH = image.size.height * containerSize.height

                                TruvideoDraggableContainer(
                                    animate = animate,
                                    enabled = drawMode == DrawMode.Picker,
                                    position = Offset(
                                        x = image.position.x * containerSize.width - context.dpToPx(10f),
                                        y = image.position.y * containerSize.height - context.dpToPx(10f)
                                    ),
                                    rotation = image.rotation,
                                    scale = image.scale,
                                    pointerInputKeys = pointerInputKeys,
                                    onChange = { dragging, position, rotation, scale ->
                                        if (!dragging) {
                                            changeImage(
                                                image.copy(
                                                    rotation = rotation,
                                                    scale = scale,
                                                    position = Offset(
                                                        (position.x + context.dpToPx(10f)) / containerSize.width,
                                                        (position.y + context.dpToPx(10f)) / containerSize.height
                                                    )
                                                )
                                            )
                                        }
                                    }
                                ) {
                                    TruvideoScaleButton(
                                        enabled = drawMode == DrawMode.Picker,
                                        onPressed = { onImagePressed(image) }
                                    ) {
                                        DrawItemComponent(
                                            enabled = drawMode == DrawMode.Picker,
                                            selected = image.id == selectedId,
                                            imageSize = Size(itemW, itemH),
                                            bitmap = image.bitmap,
                                            contentPadding = 10f
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Gesture detector
            if (drawMode == DrawMode.Pencil) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(containerSize, drawMode, drawingWidth, drawingColor) {
                            detectDragGestures(
                                onDragStart = { onDragStart(it) },
                                onDragCancel = { onDragCancel() },
                                onDragEnd = { onDragCancel() }
                            ) { change, _ ->
                                onDrag(change.position)
                                change.consume()
                            }
                        }
                )
            }

            // Temp drawing
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint()
                            if (tempDrawing != null && tempDrawingPath != null) {
                                canvas.drawPath(
                                    path = tempDrawingPath,
                                    paint = paint.apply {
                                        color = tempDrawing!!.color
                                        style = PaintingStyle.Stroke
                                        strokeWidth = tempDrawing!!.strokeWidth.dp.toPx()
                                        strokeCap = StrokeCap.Round
                                        blendMode = BlendMode.SrcOver
                                    }
                                )
                            }
                        }
                    }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.Blue,
        Color.Cyan,
        Color.Black,
        Color.White
    )
    val aspectRatio by remember { mutableFloatStateOf(9 / 40f) }
    var toolsVisible by remember { mutableStateOf(false) }
    var drawMode by remember { mutableStateOf(DrawMode.Pencil) }
    var drawingColor by remember { mutableStateOf(Color.Black) }
    var drawingWidth by remember { mutableFloatStateOf(20f) }
    var selectedId by remember { mutableStateOf("") }
    var animate by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var images by remember {
        mutableStateOf<ImmutableList<DrawingData>>(
            persistentListOf(
                DrawingData(
                    id = "1",
                    bitmap = DrawUtils.createTextImage("hola", size = 40f, color = Color.Black),
                    rotation = 0f,
                    scale = 1.0f,
                    position = Offset(0f, 0f),
                    size = Size(0.5f, 0.5f)
                )
            )
        )
    }

    TruVideoSdkTheme {
        Column(Modifier.background(Color.LightGray)) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .aspectRatio(aspectRatio)
                        .fillMaxSize()
                        .background(Color.White)
                        .align(Alignment.Center)
                ) {

                    DrawComponent(
                        animate = animate,
                        drawingColor = drawingColor,
                        drawingWidth = drawingWidth,
                        drawMode = drawMode,
                        images = images,
                        selectedId = selectedId,
                        onImagePressed = {
                            selectedId = if (selectedId == it.id) {
                                ""
                            } else {
                                it.id
                            }
                        },
                        addImage = {
                            images = images.toMutableList().apply { add(it) }.toPersistentList()
                        },
                        changeImage = { image ->
                            val index = images.indexOfFirst { image.id == it.id }
                            if (index == -1) return@DrawComponent
                            images = images.toMutableList().apply { set(index, image) }.toPersistentList()
                        }
                    )
                }

            }

            TruvideoAnimatedCollapseVisibility(toolsVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )
                ) {
                    colors.forEach {
                        Box(
                            Modifier
                                .padding(end = 4.dp)
                                .size(40.dp)
                                .background(it)
                                .border(1.dp, Color.Black)
                                .clickable {
                                    drawingColor = it
                                }
                        )
                    }
                }

                Text(
                    "Drawing Width: $drawingWidth",
                    modifier = Modifier.clickable {
                        drawingWidth = when (drawingWidth) {
                            1f -> 2f
                            2f -> 3f
                            3f -> 4f
                            4f -> 5f
                            5f -> 6f
                            6f -> 7f
                            7f -> 8f
                            8f -> 9f
                            9f -> 1f
                            else -> 1f
                        }
                    }
                )

                Text(
                    "Mode: ${drawMode.name}",
                    modifier = Modifier.clickable {
                        val index = (drawMode.ordinal + 1) % DrawMode.entries.size
                        drawMode = DrawMode.entries[index]
                    }
                )

                Text("Tools Visible: $toolsVisible", Modifier.clickable {
                    toolsVisible = !toolsVisible
                    animate = false
                    scope.launch {
                        delay(500)
                        animate = false
                    }
                })
            }

        }
    }
}

private fun pointsToPath(points: ImmutableList<Offset>): Path {
    val result = Path()
    points.forEachIndexed { index, offset ->
        if (index == 0) {
            result.moveTo(offset.x, offset.y)
        } else {
            result.lineTo(offset.x, offset.y)
        }
    }


    return result
}

private fun getTopLeft(points: ImmutableList<Offset>): Offset {
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    points.forEach {
        minX = minOf(minX, it.x)
        minY = minOf(minY, it.y)
    }
    return Offset(minX, minY)
}

private fun getBottomRight(points: ImmutableList<Offset>): Offset {
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    points.forEach {
        maxX = maxOf(maxX, it.x)
        maxY = maxOf(maxY, it.y)
    }
    return Offset(maxX, maxY)
}

private fun generateImage(
    context: Context,
    containerSize: Size,
    data: DrawingPencilData
): DrawingData {
    val offset = Offset(context.dpToPx(data.strokeWidth * 0.5f), context.dpToPx(data.strokeWidth * 0.5f))

    val tlValue = getTopLeft(data.points)
    val tl = Offset(tlValue.x * containerSize.width, tlValue.y * containerSize.height) - offset

    val brValue = getBottomRight(data.points)
    val br = Offset(brValue.x * containerSize.width, brValue.y * containerSize.height) + offset

    val width = (br.x - tl.x).toInt()
    val height = (br.y - tl.y).toInt()

    val path = android.graphics.Path()
    data.points.forEachIndexed { index, point ->
        val x = point.x * containerSize.width - tl.x
        val y = point.y * containerSize.height - tl.y

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = data.color
        style = PaintingStyle.Stroke
        strokeWidth = context.dpToPx(data.strokeWidth)
        strokeCap = StrokeCap.Round
    }

    canvas.drawPath(path, paint.asFrameworkPaint())


    return DrawingData(
        id = System.currentTimeMillis().toString(),
        bitmap = bitmap,
        rotation = 0f,
        scale = 1.0f,
        size = Size(
            width / containerSize.width,
            height / containerSize.height
        ),
        position = Offset(
            tl.x / containerSize.width,
            tl.y / containerSize.height
        ),
    )
}

internal data class DrawingPencilData(
    val id: String,
    val points: ImmutableList<Offset>,
    val color: Color,
    val strokeWidth: Float
)

internal data class DrawingData(
    val id: String,
    val bitmap: Bitmap,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val position: Offset,
    val size: Size,
)