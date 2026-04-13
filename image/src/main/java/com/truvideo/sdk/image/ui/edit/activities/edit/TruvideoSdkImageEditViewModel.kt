package com.truvideo.sdk.image.ui.edit.activities.edit

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truvideo.sdk.image.TruvideoSdkImage
import com.truvideo.sdk.image.model.ImageFilterType
import com.truvideo.sdk.image.model.TruvideoSdkImageCropInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageInformation
import com.truvideo.sdk.image.model.TruvideoSdkImageOutputFormat
import com.truvideo.sdk.image.model.TruvideoSdkImageRotation
import com.truvideo.sdk.image.ui.edit.activities.edit.model.ActivityTab
import com.truvideo.sdk.image.ui.edit.activities.edit.model.CropData
import com.truvideo.sdk.image.ui.edit.activities.edit.model.DrawData
import com.truvideo.sdk.image.ui.edit.activities.edit.model.DrawMode
import com.truvideo.sdk.image.ui.edit.activities.edit.model.ImageData
import com.truvideo.sdk.image.ui.edit.activities.edit.model.PreviewData
import com.truvideo.sdk.image.ui.edit.components.crop.model.CropInformation
import com.truvideo.sdk.image.ui.edit.components.draw.DrawingData
import com.truvideo.sdk.image.usecases.BitmapFilterUseCase
import com.truvideo.sdk.image.usecases.BitmapResizeUseCase
import com.truvideo.sdk.image.usecases.GetBitmapUseCase
import com.truvideo.sdk.image.usecases.GetInformationUseCase
import com.truvideo.sdk.image.usecases.SaveBitmapUseCase
import com.truvideo.sdk.image.utils.CropUtils
import com.truvideo.sdk.image.utils.DrawUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.graphics.createBitmap

private val getInformationUseCase = GetInformationUseCase()
private val getBitmapUseCase = GetBitmapUseCase()
private val saveBitmapUseCase = SaveBitmapUseCase()
private val bitmapResizeUseCase = BitmapResizeUseCase()
private val bitmapFilterUseCase = BitmapFilterUseCase()
private const val bitmapThumbnailWidth = 1000

internal class TruvideoSdkImageEditViewModel(
    private val inputPath: String,
    private val outputPath: String,
    private val isPreviewMode: Boolean = false,
) : ViewModel() {


    private val _isInitializing = MutableStateFlow(true)
    private val _isProcessing = MutableStateFlow(false)
    private val _activityTab = MutableStateFlow<ActivityTab?>(null)
    private val _successPath = MutableStateFlow("")
    private val _error = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")
    private val _isPreviewVisible = MutableStateFlow(false)
    private val _previewDataHistory =
        MutableStateFlow<ImmutableList<PreviewData>>(persistentListOf())
    private val _data = MutableStateFlow(
        ImageData(
            preview = PreviewData.empty(),
            crop = CropData.empty(),
            draw = DrawData.empty()
        ),
    )

    private val _cropDataHistory = MutableStateFlow<ImmutableList<CropData>>(persistentListOf())
    private val _isCropping = MutableStateFlow(false)

    private val _drawDataHistory = MutableStateFlow<ImmutableList<DrawData>>(persistentListOf())
    private val _isDrawing = MutableStateFlow(false)
    private val _drawMode = MutableStateFlow(DrawMode.Pencil)
    private val _drawCanvasSize = MutableStateFlow(Size.Zero)
    private val _drawSelectedId = MutableStateFlow("")
    private val _drawColor = MutableStateFlow(Color.White)
    private val _drawWidth = MutableStateFlow(4f)

    private var _isInitialized = false

    private val _isFiltering = MutableStateFlow(false)
    private val _filterType = MutableStateFlow(ImageFilterType.Default)
    private val _filterIntensity = MutableStateFlow(1f)

    val isPreviewVisible = _isPreviewVisible.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _isPreviewVisible.value
    )

    val previewData = _data.map { it.preview }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _data.value.preview
    )

    val cropData = _data.map { it.crop }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _data.value.crop
    )

    val isDrawing = _isDrawing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val drawData = _data.map { it.draw }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _data.value.draw
    )

    val drawColor = _drawColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _drawColor.value
    )

    val drawWidth = _drawWidth.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _drawWidth.value
    )

    val drawCanvasSize = _drawCanvasSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _drawCanvasSize.value
    )

    val drawSelectedId = _drawSelectedId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _drawSelectedId.value
    )

    val isFiltering = _isFiltering.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val filterType = _filterType.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _filterType.value
    )

    val filterIntensity = _filterIntensity.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _filterIntensity.value
    )

    val isInitializing = _isInitializing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val isProcessing = _isProcessing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val tab = _activityTab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _activityTab.value
    )

    val successPath = _successPath.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val error = _error.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val errorMessage = _errorMessage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val isCropping = _isCropping.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _isCropping.value
    )

    val drawMode = _drawMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _drawMode.value
    )

    val isUndoVisible = combine(
        _activityTab,
        _previewDataHistory,
        _cropDataHistory,
        _drawDataHistory
    ) { mode, previewDataHistory, cropDataHistory, drawDataHistory ->
        isUndoVisible(
            mode = mode,
            previewDataHistory = previewDataHistory,
            cropDataHistory = cropDataHistory,
            drawDataHistory = drawDataHistory
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = isUndoVisible(
                mode = _activityTab.value,
                previewDataHistory = _previewDataHistory.value,
                cropDataHistory = _cropDataHistory.value,
                drawDataHistory = _drawDataHistory.value
            )
        )

    suspend fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        _isInitializing.value = true
        _isPreviewVisible.value = false
        if (!isPreviewMode) {
            val information = getInformationUseCase(inputPath)
            val bitmap = getBitmapUseCase(inputPath)
            val thumbnail = bitmapResizeUseCase(bitmap, bitmapThumbnailWidth)
            _data.value = ImageData(
                preview = PreviewData(
                    bitmap = bitmap,
                    thumbnailBitmap = thumbnail,
                    information = information
                ),
                crop = CropData.empty(),
                draw = DrawData.empty()
            )
            _isPreviewVisible.value = true
        } else {
            _data.value = ImageData(
                preview = PreviewData.empty(),
                crop = CropData.empty(),
                draw = DrawData.empty()
            )
        }
        _isInitializing.value = false
        _isPreviewVisible.value = true
    }

    private fun calculateOutputFormat(path: String): TruvideoSdkImageOutputFormat {
        val file = File(path)
        val extension = file.extension
        return when (extension) {
            "png" -> TruvideoSdkImageOutputFormat.PNG
            "jpg", "jpeg" -> TruvideoSdkImageOutputFormat.JPG
            else -> TruvideoSdkImageOutputFormat.PNG
        }
    }

    fun apply() {
        if (_isProcessing.value) return

        viewModelScope.launch {

            try {
                _successPath.value = ""
                _error.value = false
                _isProcessing.value = true

                val preview = _data.value.preview;

                saveBitmapUseCase(
                    bitmap = preview.bitmap,
                    resultPath = outputPath,
                    outputFormat = calculateOutputFormat(outputPath),
                    compressionQuality = 90
                )

                _isProcessing.value = false
                _successPath.value = outputPath

            } catch (exception: Exception) {
                exception.printStackTrace()
                _errorMessage.value = exception.localizedMessage ?: "Unknown error"
                _successPath.value = ""
                _error.value = true
                _isProcessing.value = false
            }
        }
    }


    fun updateTab(value: ActivityTab?) {
        _activityTab.value = value

        _data.value = _data.value.copy(
            crop = CropData.empty(),
            draw = DrawData.empty()
        )
        clearCrop()
        clearDraw()
        clearFilter()
    }

    // -----------------------------
    // Crop
    // -----------------------------

    private fun clearCrop() {
        _cropDataHistory.value = persistentListOf()
    }

    fun rotateLeft() {
        val data = _data.value.crop
        _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
            add(data)
        }.toPersistentList()

        _data.value = _data.value.copy(
            crop = data.copy(rotation = data.rotation - 90)
        )
    }

    fun rotateRight() {
        val data = _data.value.crop
        _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
            add(data)
        }.toPersistentList()

        _data.value = _data.value.copy(
            crop = data.copy(rotation = data.rotation + 90)
        )
    }

    fun toggleFlippedHorizontal() {
        val data = _data.value.crop
        _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
            add(data)
        }.toPersistentList()

        when (fixRotation(data.rotation)) {
            90f, 270f -> {
                _data.value = _data.value.copy(
                    crop = data.copy(verticalFlip = !data.verticalFlip)
                )
            }

            else -> {
                _data.value = _data.value.copy(
                    crop = data.copy(horizontalFlip = !data.horizontalFlip)
                )
            }
        }
    }

    fun toggleFlippedVertical() {
        val data = _data.value.crop
        _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
            add(data)
        }.toPersistentList()

        when (fixRotation(data.rotation)) {
            90f, 270f -> {
                _data.value = _data.value.copy(
                    crop = data.copy(horizontalFlip = !data.horizontalFlip)
                )
            }

            else -> {
                _data.value = _data.value.copy(
                    crop = data.copy(verticalFlip = !data.verticalFlip)
                )
            }
        }
    }

    fun updateCropInformation(value: CropInformation) {
        val data = _data.value.crop
        _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
            add(data)
        }.toPersistentList()
        _data.value = _data.value.copy(
            crop = data.copy(information = value)
        )
    }

    fun applyCrop() {
        viewModelScope.launch {
            try {
                _isCropping.value = true
                _isPreviewVisible.value = false
                val data = _data.value.preview
                val bitmap = data.bitmap

                val cropData = _data.value.crop
                var crop = cropData.information

                // Rotate
                val fixedRotation = fixRotation(cropData.rotation)
                if (fixedRotation != 0f) {
                    crop = CropUtils.rotate(crop, fixedRotation)
                }

                // Flip horizontal
                if (cropData.horizontalFlip) {
                    crop = CropUtils.flipHorizontal(crop)
                }

                // Flip vertical
                if (cropData.verticalFlip) {
                    crop = CropUtils.flipVertical(crop)
                }

                // Calculate width
                val imageWidth = when (fixedRotation) {
                    90f, 270f -> crop.height
                    else -> crop.width
                }

                // Calculate height
                val imageHeight = when (fixedRotation) {
                    90f, 270f -> crop.width
                    else -> crop.height
                }

                val newBitmap = if (isPreviewMode) {
                    val w = imageWidth * crop.width
                    val h = imageHeight * crop.height
                    when (fixedRotation) {
                        90f, 270f -> createBitmap(h.toInt(), w.toInt())
                        else -> createBitmap(w.toInt(), h.toInt())
                    }
                } else {
                    TruvideoSdkImage.editBitmap(
                        bitmap = bitmap,
                        rotation = when (fixedRotation) {
                            90f -> TruvideoSdkImageRotation.ROTATE_90
                            180f -> TruvideoSdkImageRotation.ROTATE_180
                            270f -> TruvideoSdkImageRotation.ROTATE_270
                            else -> null
                        },
                        horizontalFlip = cropData.horizontalFlip,
                        verticalFlip = cropData.verticalFlip,
                        cropInformation = TruvideoSdkImageCropInformation(
                            left = crop.topLeft.x,
                            top = crop.topLeft.y,
                            width = crop.width,
                            height = crop.height
                        )
                    )
                }

                val newPreview = PreviewData(
                    bitmap = newBitmap,
                    thumbnailBitmap = bitmapResizeUseCase(newBitmap, bitmapThumbnailWidth),
                    information = TruvideoSdkImageInformation(
                        width = newBitmap.width,
                        height = newBitmap.height,
                        path = "",
                        size = 0,
                    )
                )

                _previewDataHistory.value = _previewDataHistory.value.toMutableList().apply {
                    add(data)
                }.toPersistentList()

                _data.value = _data.value.copy(
                    preview = newPreview,
                    crop = CropData.empty(),
                    draw = DrawData.empty()
                )

                _cropDataHistory.value = persistentListOf()
                _activityTab.value = null
                _isCropping.value = false

                if (!isPreviewMode) {
                    delay(500)
                    _isPreviewVisible.value = true
                }

                Log.d("TruvideoSdkImage", "Crop done. Added preview history")
            } catch (exception: Exception) {
                exception.printStackTrace()
                _isCropping.value = false
                _isPreviewVisible.value = true
            }
        }
    }


    // -----------------------------
    // Draw
    // -----------------------------

    private fun clearDraw() {
        _drawDataHistory.value = persistentListOf()
        _drawMode.value = DrawMode.Pencil
        _drawColor.value = Color.White
        _drawWidth.value = 4f
        updateDrawSelectedId("")
    }

    fun updateDrawMode(value: DrawMode) {
        _drawMode.value = value
        if (value != DrawMode.Picker) {
            _drawSelectedId.value = ""
        }
    }

    fun updateDrawColor(color: Color) {
        _drawColor.value = color
    }

    fun updateDrawWidth(width: Float) {
        _drawWidth.value = width
    }

    fun updateDrawCanvasSize(size: Size) {
        _drawCanvasSize.value = size
    }

    fun updateDrawSelectedId(id: String) {
        val effectiveId = if (id == _drawSelectedId.value) "" else id
        _drawSelectedId.value = effectiveId
    }

    fun updateDrawingImageRotation(angle: Float) {
        val id = _drawSelectedId.value
        if (id.trim().isEmpty()) return

        val draw = _data.value.draw
        val image = draw.images.find { it.id == id } ?: return

        _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
            add(draw)
        }.toPersistentList()

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList()
                    .apply {
                        set(indexOf(image), image.copy(rotation = angle))
                    }
                    .toPersistentList()
            ),
        )
    }

    fun updateDrawingImageScale(scale: Float) {
        val id = _drawSelectedId.value
        if (id.trim().isEmpty()) return

        val draw = _data.value.draw
        val image = draw.images.find { it.id == id } ?: return
        _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
            add(draw)
        }.toPersistentList()

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList()
                    .apply {
                        set(indexOf(image), image.copy(scale = scale))
                    }
                    .toPersistentList()
            )
        )
    }

    fun updateDrawingImageAlignment(alignment: Alignment) {
        val id = _drawSelectedId.value
        if (id.trim().isEmpty()) return

        val draw = _data.value.draw
        val image = draw.images.find { it.id == id } ?: return

        val w = image.size.width
        val scaleW = w * image.scale
        val diffW = scaleW - w

        val h = image.size.height
        val scaleH = h * image.scale
        val diffH = scaleH - h

        val startX = 0f + diffW * 0.5f
        val centerX = (1.0f - scaleW) * 0.5f + diffW * 0.5f
        val endX = 1.0f - scaleW + diffW * 0.5f

        val startY = 0f + diffH * 0.5f
        val centerY = (1.0f - scaleH) * 0.5f + diffH * 0.5f
        val endY = 1.0f - scaleH + diffH * 0.5f

        val newPosition = when (alignment) {
            Alignment.TopStart -> Offset(startX, startY)
            Alignment.TopCenter -> Offset(centerX, startY)
            Alignment.TopEnd -> Offset(endX, startY)
            Alignment.CenterStart -> Offset(startX, centerY)
            Alignment.Center -> Offset(centerX, centerY)
            Alignment.CenterEnd -> Offset(endX, centerY)
            Alignment.BottomStart -> Offset(startX, endY)
            Alignment.BottomCenter -> Offset(centerX, endY)
            Alignment.BottomEnd -> Offset(endX, endY)
            else -> Offset.Zero
        }

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList().apply {
                    set(indexOf(image), image.copy(position = newPosition))
                }.toPersistentList()
            )
        )
    }

    fun applyDraw() {
        viewModelScope.launch {
            _isDrawing.value = true

            try {
                // Calculate
                val preview = _data.value.preview
                val draw = _data.value.draw
                val bitmap = DrawUtils.createImage(
                    originalBitmap = preview.bitmap,
                    images = draw.images
                )

                val bitmapThumbnail = bitmapResizeUseCase(bitmap, bitmapThumbnailWidth)

                // Update preview history
                _previewDataHistory.value = _previewDataHistory.value.toMutableList().apply {
                    add(preview)
                }.toPersistentList()

                // Update preview
                _drawDataHistory.value = persistentListOf()
                _data.value = _data.value.copy(
                    preview = PreviewData(
                        bitmap = bitmap,
                        thumbnailBitmap = bitmapThumbnail,
                        information = TruvideoSdkImageInformation(
                            width = bitmap.width,
                            height = bitmap.height,
                            path = "",
                            size = 0,
                        )
                    ),
                    crop = CropData.empty(),
                    draw = DrawData.empty()
                )

                clearDraw()

                // Restore preview
                _activityTab.value = null
                _isDrawing.value = false
            } catch (exception: Exception) {
                exception.printStackTrace()
                _isDrawing.value = false
            }
        }
    }

    fun addDrawingImage(image: DrawingData) {
        val draw = _data.value.draw
        _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
            add(draw)
        }.toPersistentList()

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList().apply {
                    add(image)
                }.toPersistentList()
            )
        )
    }

    fun changeDrawingImage(image: DrawingData) {
        val draw = _data.value.draw
        val index = draw.images.indexOfFirst { it.id == image.id }
        if (index == -1) return

        _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
            add(draw)
        }.toPersistentList()

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList().apply {
                    set(index, image)
                }.toPersistentList()
            ),
        )
    }

    fun removeDrawingImage() {
        val id = _drawSelectedId.value
        if (id.trim().isEmpty()) return

        val draw = _data.value.draw
        val index = draw.images.indexOfFirst { it.id == id }
        if (index == -1) return

        _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
            add(draw)
        }.toPersistentList()

        _data.value = _data.value.copy(
            draw = draw.copy(
                images = draw.images.toMutableList().apply {
                    removeAt(index)
                }.toPersistentList()
            ),
        )
        _drawSelectedId.value = ""
    }

    // -----------------------------
    // Filter
    // -----------------------------

    private fun clearFilter() {
        _filterType.value = ImageFilterType.Default
        _data.value = _data.value.copy(
            preview = _data.value.preview.copy(
                thumbnailFilteredBitmap = null
            )
        )
    }

    fun updateFilterType(type: ImageFilterType) {
        if (_filterType.value == type) return

        _filterType.value = type
        _filterIntensity.value = 1f
        viewModelScope.launch {
            val preview = _data.value.preview
            val bitmap = bitmapFilterUseCase(
                bitmap = preview.thumbnailBitmap,
                type = _filterType.value,
                intensity = _filterIntensity.value
            )
            val newPreview = preview.copy(thumbnailFilteredBitmap = bitmap)
            _data.value = _data.value.copy(preview = newPreview)
        }
    }

    fun updateFilterIntensity(value: Float) {
        if (_filterIntensity.value == value) return

        _filterIntensity.value = value
        viewModelScope.launch {
            val preview = _data.value.preview
            val bitmap = bitmapFilterUseCase(
                bitmap = preview.thumbnailBitmap,
                type = _filterType.value,
                intensity = _filterIntensity.value
            )
            val newPreview = preview.copy(thumbnailFilteredBitmap = bitmap)
            _data.value = _data.value.copy(preview = newPreview)
        }
    }

    fun applyFilter() {
        if (_isFiltering.value) return

        viewModelScope.launch {
            _isFiltering.value = true

            try {
                val preview = _data.value.preview
                val bitmap = bitmapFilterUseCase(preview.bitmap, _filterType.value)
                val bitmapThumbnail = bitmapResizeUseCase(bitmap, bitmapThumbnailWidth)

                // Update preview history
                _previewDataHistory.value = _previewDataHistory.value.toMutableList().apply {
                    add(preview)
                }.toPersistentList()

                // Update preview
                _data.value = _data.value.copy(
                    preview = PreviewData(
                        bitmap = bitmap,
                        thumbnailBitmap = bitmapThumbnail,
                        information = TruvideoSdkImageInformation(
                            width = bitmap.width,
                            height = bitmap.height,
                            path = "",
                            size = 0,
                        )
                    )
                )

                // Restore preview
                _isFiltering.value = false
                clearFilter()
            } catch (exception: Exception) {
                exception.printStackTrace()
                _isFiltering.value = false
            }
        }
    }

    // -----------------------------
    // Queue
    // -----------------------------

    fun undo() {
        when (_activityTab.value) {
            null -> {
                val history = _previewDataHistory.value
                if (history.isEmpty()) return

                val last = _previewDataHistory.value.last()
                _previewDataHistory.value = _previewDataHistory.value.toMutableList().apply {
                    removeAt(lastIndex)
                }.toPersistentList()
                _data.value = _data.value.copy(preview = last)
            }

            ActivityTab.Crop -> {
                val history = _cropDataHistory.value
                if (history.isEmpty()) return

                val last = _cropDataHistory.value.last()
                _cropDataHistory.value = _cropDataHistory.value.toMutableList().apply {
                    removeAt(lastIndex)
                }.toPersistentList()
                _data.value = _data.value.copy(crop = last)
            }

            ActivityTab.Draw -> {
                val history = _drawDataHistory.value
                if (history.isEmpty()) return

                val last = _drawDataHistory.value.last()
                _drawDataHistory.value = _drawDataHistory.value.toMutableList().apply {
                    removeAt(lastIndex)
                }.toPersistentList()
                _data.value = _data.value.copy(draw = last)
            }

//            ActivityTab.Filter -> {
//
//            }
        }
    }
}

private fun isUndoVisible(
    mode: ActivityTab?,
    previewDataHistory: ImmutableList<PreviewData>,
    cropDataHistory: ImmutableList<CropData>,
    drawDataHistory: ImmutableList<DrawData>
): Boolean {
    return when (mode) {
        null -> previewDataHistory.isNotEmpty()
        ActivityTab.Crop -> cropDataHistory.isNotEmpty()
        ActivityTab.Draw -> drawDataHistory.isNotEmpty()
//        ActivityTab.Filter -> false
    }
}

private fun fixRotation(value: Float): Float {
    var f = value % 360
    f = if (f < 0) {
        f + 360
    } else {
        f
    }

    return f
}


