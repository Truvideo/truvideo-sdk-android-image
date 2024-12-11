package com.truvideo.sdk.image.ui.edit.activities.edit

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.truvideo.sdk.components.animated_fade_visibility.TruvideoAnimatedFadeVisibility
import com.truvideo.sdk.image.ui.edit.activities.edit.model.ActivityTab
import com.truvideo.sdk.image.ui.edit.activities.edit.model.DrawMode
import com.truvideo.sdk.image.ui.edit.components.app_bar.AppBar
import com.truvideo.sdk.image.ui.edit.components.crop.Crop
import com.truvideo.sdk.image.ui.edit.components.crop.CropAppBar
import com.truvideo.sdk.image.ui.edit.components.crop.CropNavigationBar
import com.truvideo.sdk.image.ui.edit.components.draw.DrawAppBar
import com.truvideo.sdk.image.ui.edit.components.draw.DrawColor2
import com.truvideo.sdk.image.ui.edit.components.draw.DrawComponent
import com.truvideo.sdk.image.ui.edit.components.draw.DrawNavigationBar
import com.truvideo.sdk.image.ui.edit.components.draw.DrawOptions
import com.truvideo.sdk.image.ui.edit.components.draw.DrawTextDialog
import com.truvideo.sdk.image.ui.edit.components.draw.DrawWidth
import com.truvideo.sdk.image.ui.edit.components.preview.PicturePreview
import com.truvideo.sdk.image.ui.edit.components.tab_bar.TabBar
import com.truvideo.sdk.image.ui.edit.theme.TruVideoSdkTheme

internal class TruvideoSdkImageEditActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val paramsJson: String = intent?.getStringExtra(TruvideoSdkImageEditContract.PARAMS)!!
        val params = TruvideoSdkImageEditParams.fromJson(paramsJson)

        val viewModel = TruvideoSdkImageEditViewModel(
            inputPath = params.inputPath,
            outputPath = params.outputPath,
        )

        setContent {
            TruVideoSdkTheme {
                Content(viewModel)
            }
        }
    }

    @Composable
    private fun Content(viewModel: TruvideoSdkImageEditViewModel) {
        val context = LocalContext.current
        LaunchedEffect(Unit) { viewModel.initialize() }

        val isInitializing by remember(viewModel) { viewModel.isInitializing }.collectAsStateWithLifecycle()
        val isProcessing by remember(viewModel) { viewModel.isProcessing }.collectAsStateWithLifecycle()
        val successPath by remember(viewModel) { viewModel.successPath }.collectAsStateWithLifecycle()
        val error by remember(viewModel) { viewModel.error }.collectAsStateWithLifecycle()
        val errorMessage by remember(viewModel) { viewModel.errorMessage }.collectAsStateWithLifecycle()
        val tab by remember(viewModel) { viewModel.tab }.collectAsStateWithLifecycle()

        val isUndoVisible by remember { viewModel.isUndoVisible }.collectAsStateWithLifecycle()
        val isPreviewVisible by remember(viewModel) { viewModel.isPreviewVisible }.collectAsStateWithLifecycle()
        val previewData by remember(viewModel) { viewModel.previewData }.collectAsStateWithLifecycle()
        val cropData by remember(viewModel) { viewModel.cropData }.collectAsStateWithLifecycle()
        val drawData by remember(viewModel) { viewModel.drawData }.collectAsStateWithLifecycle()
        val isCropping by remember(viewModel) { viewModel.isCropping }.collectAsStateWithLifecycle()

        val isDrawing by remember(viewModel) { viewModel.isDrawing }.collectAsStateWithLifecycle()
        val drawMode by remember(viewModel) { viewModel.drawMode }.collectAsStateWithLifecycle()
        val drawColor by remember(viewModel) { viewModel.drawColor }.collectAsStateWithLifecycle()
        val drawWidth by remember(viewModel) { viewModel.drawWidth }.collectAsStateWithLifecycle()
        val drawSelectedId by remember(viewModel) { viewModel.drawSelectedId }.collectAsStateWithLifecycle()
        val drawCanvasSize by remember(viewModel) { viewModel.drawCanvasSize }.collectAsStateWithLifecycle()
        var isCreatingText by remember { mutableStateOf(false) }

//        val isFiltering by remember(viewModel) { viewModel.isFiltering }.collectAsStateWithLifecycle()
//        val filterType by remember(viewModel) { viewModel.filterType }.collectAsStateWithLifecycle()

        BackHandler(true) {
            when (tab) {
                ActivityTab.Crop -> {
                    if (isCropping) {
                        return@BackHandler
                    }

                    viewModel.updateTab(null)
                }

                ActivityTab.Draw -> {
                    if (isDrawing) {
                        return@BackHandler
                    }

                    viewModel.updateTab(null)
                }

//                ActivityTab.Filter -> {
//                    if (isFiltering) {
//                        return@BackHandler
//                    }
//
//                    viewModel.updateMode(null)
//                }

                null -> {
                    if (isInitializing || isProcessing) {
                        return@BackHandler
                    }

                    finish()
                }
            }
        }

        LaunchedEffect(successPath) {
            if (successPath.trim().isNotEmpty()) {
                Log.d("TruvideoSdkImage", "Success: $successPath")
                setResult(RESULT_OK, Intent().apply { putExtra(TruvideoSdkImageEditContract.OUTPUT_PATH, successPath) })
                finish()
            }
        }

        LaunchedEffect(error) {
            if (error) {
                AlertDialog.Builder(context)
                    .setMessage(errorMessage)
                    .setPositiveButton("Accept") { _, _ ->

                    }
                    .show()
            }
        }

        Scaffold(
            containerColor = Color.Black,
            contentColor = Color.Black,
            modifier = Modifier.fillMaxSize()
        ) { screenPadding ->
            Column {

                // App Bar
                AnimatedContent(targetState = tab, label = "app-bar") { modeTarget ->
                    when (modeTarget) {
                        ActivityTab.Crop -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = screenPadding.calculateTopPadding())
                            ) {
                                CropAppBar(
                                    undoVisible = isUndoVisible,
                                    onButtonUndoPressed = { viewModel.undo() },
                                    onButtonRotateLeftPressed = { viewModel.rotateLeft() },
                                    onButtonRotateRightPressed = { viewModel.rotateRight() },
                                    onButtonFlipHorizontalPressed = { viewModel.toggleFlippedHorizontal() },
                                    onButtonFlipVerticalPressed = { viewModel.toggleFlippedVertical() },
                                )
                            }
                        }

                        ActivityTab.Draw -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = screenPadding.calculateTopPadding())
                            ) {
                                DrawAppBar(
                                    undoVisible = isUndoVisible,
                                    buttonPickerVisible = drawData.images.isNotEmpty(),
                                    onButtonUndoPressed = { viewModel.undo() },
                                    drawMode = drawMode,
                                    onButtonPencilPressed = { viewModel.updateDrawMode(DrawMode.Pencil) },
                                    onButtonPickerPressed = { viewModel.updateDrawMode(DrawMode.Picker) },
                                    onButtonTextPressed = { isCreatingText = true }
                                )
                            }
                        }

//                        ActivityTab.Filter -> {
//                            Box(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .statusBarsPadding()
//                            )
//                        }

                        null -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = screenPadding.calculateTopPadding())
                        ) {
                            AppBar(
                                buttonUndoEnabled = !isInitializing && !isProcessing,
                                buttonUndoVisible = isUndoVisible,
                                onButtonUndoPressed = { viewModel.undo() },
                                buttonCloseEnabled = !isInitializing && !isProcessing,
                                onButtonClosePressed = { finish() },
                                buttonContinueEnabled = !isInitializing && !isProcessing,
                                onButtonContinuePressed = { viewModel.apply() }
                            )
                        }
                    }
                }

                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isInitializing && isPreviewVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            // Image
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                            ) {
//                                val effectiveBitmap = when (mode) {
//                                    ActivityTab.Filter -> previewData.thumbnailFilteredBitmap ?: previewData.thumbnailBitmap
//                                    else -> previewData.bitmap
//                                }
                                val effectiveBitmap = previewData.bitmap

                                PicturePreview(
                                    bitmap = effectiveBitmap,
                                    aspectRatio = previewData.information.aspectRatio,
                                    rotation = cropData.rotation,
                                    horizontalFlip = cropData.horizontalFlip,
                                    verticalFlip = cropData.verticalFlip
                                ) {
                                    Box(Modifier.fillMaxSize()) {
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = tab == ActivityTab.Crop,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            Crop(
                                                aspectRatio = previewData.information.aspectRatio,
                                                information = cropData.information,
                                                onInformationChange = { dragging, info ->
                                                    if (!dragging) {
                                                        viewModel.updateCropInformation(info)
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }

                            // Draw
                            androidx.compose.animation.AnimatedVisibility(
                                visible = tab == ActivityTab.Draw,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    Box(
                                        Modifier
                                            .padding(16.dp)
                                            .aspectRatio(previewData.information.aspectRatio)
                                            .fillMaxWidth()
                                            .clipToBounds()
                                            .align(Alignment.Center)
                                            .border(width = 2.dp, color = Color.White)

                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .onSizeChanged { viewModel.updateDrawCanvasSize(it.toSize()) }
                                        ) {
                                            DrawComponent(
                                                animate = drawSelectedId.trim() != "",
                                                drawMode = drawMode,
                                                selectedId = drawSelectedId,
                                                drawingColor = drawColor,
                                                drawingWidth = drawWidth,
                                                images = drawData.images,
                                                addImage = { viewModel.addDrawingImage(it) },
                                                changeImage = { viewModel.changeDrawingImage(it) },
                                                onImagePressed = { viewModel.updateDrawSelectedId(it.id) }
                                            )
                                        }
                                    }
                                }
                            }


//                            // Filter
//                            androidx.compose.animation.AnimatedVisibility(
//                                visible = mode == ActivityTab.Filter,
//                                enter = fadeIn(),
//                                exit = fadeOut()
//                            ) {
//                                Box(modifier = Modifier.fillMaxSize()) {
//                                    Box(
//                                        modifier = Modifier
//                                            .padding(bottom = 16.dp)
//                                            .clip(RoundedCornerShape(4.dp))
//                                            .background(TruvideoColors.gray)
//                                            .padding(horizontal = 8.dp, vertical = 4.dp)
//                                            .align(Alignment.BottomCenter)
//                                    ) {
//                                        Text(
//                                            filterType.displayName.uppercase(),
//                                            color = Color.White,
//                                            style = MaterialTheme.typography.bodyMedium
//                                        )
//                                    }
//                                }
//                            }
                        }
                    }
                }

                // Navigator
                AnimatedContent(targetState = tab, label = "mode") { modeTarget ->
                    when (modeTarget) {
                        ActivityTab.Crop -> Box(modifier = Modifier.navigationBarsPadding()) {
                            CropNavigationBar(
                                enabled = !isCropping,
                                onButtonCancelPressed = { viewModel.updateTab(null) },
                                onButtonCropPressed = { viewModel.applyCrop() }
                            )
                        }

                        ActivityTab.Draw ->
                            Box(modifier = Modifier.navigationBarsPadding()) {
                                Column {
                                    AnimatedContent(targetState = drawMode, label = "draw-mode") { drawModeTarget ->
                                        when (drawModeTarget) {
                                            DrawMode.Pencil -> Column {
                                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                                    DrawWidth(
                                                        value = drawWidth,
                                                        color = drawColor,
                                                        onValueChange = { viewModel.updateDrawWidth(it) }
                                                    )
                                                }

                                                DrawColor2(
                                                    color = drawColor,
                                                    onColorPressed = { viewModel.updateDrawColor(it) }
                                                )
                                            }

                                            DrawMode.Picker -> {
                                                val image = remember(drawData, drawSelectedId) {
                                                    drawData.images.find { it.id == drawSelectedId }
                                                }

                                                val withImage = image != null

                                                AnimatedContent(targetState = withImage, label = "draw-image-select") { withImageTarget ->
                                                    if (withImageTarget) {
                                                        DrawOptions(
                                                            currentRotation = image?.rotation ?: 0f,
                                                            currentScale = image?.scale ?: 0f,
                                                            onButtonBackPressed = { viewModel.updateDrawSelectedId("") },
                                                            changeRotation = { viewModel.updateDrawingImageRotation(it) },
                                                            changeScale = { viewModel.updateDrawingImageScale(it) },
                                                            changeAlignment = { viewModel.updateDrawingImageAlignment(it) },
                                                            delete = { viewModel.removeDrawingImage() }
                                                        )

                                                    } else {
                                                        Box(modifier = Modifier.fillMaxWidth())
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    DrawNavigationBar(
                                        enabled = !isDrawing,
                                        onButtonCancelPressed = { viewModel.updateTab(null) },
                                        onButtonApplyPressed = { viewModel.applyDraw() }
                                    )
                                }
                            }

//                        ActivityTab.Filter -> Box(modifier = Modifier.navigationBarsPadding()) {
//                            Column {
//                                val intensity by remember(viewModel) { viewModel.filterIntensity }.collectAsStateWithLifecycle()
//                                FilterOptions(
//                                    bitmap = previewData.bitmap,
//                                    type = filterType,
//                                    onTypeChange = { viewModel.updateFilterType(it) },
//                                    intensity = intensity,
//                                    onIntensityChange = { viewModel.updateFilterIntensity(it) }
//                                )
//
//                                FilterNavigationBar(
//                                    enabled = !isFiltering,
//                                    onButtonCancelPressed = { viewModel.updateMode(null) },
//                                    onButtonApplyPressed = { viewModel.applyFilter() }
//                                )
//                            }
//                        }

                        null -> TabBar(
                            enabled = !isInitializing && !isProcessing,
                            tab = tab,
                            bottomPadding = screenPadding.calculateBottomPadding(),
                            onChange = { viewModel.updateTab(it) },
                        )
                    }
                }
            }


            // Panel create text
            TruvideoAnimatedFadeVisibility(isCreatingText) {
                Box(
                    modifier = Modifier.padding(
                        top = screenPadding.calculateTopPadding(),
                        bottom = screenPadding.calculateBottomPadding()
                    )
                ) {
                    DrawTextDialog(
                        dismiss = { isCreatingText = false },
                        create = {
                            viewModel.addDrawingImage(it)
                            isCreatingText = false
                        },
                        containerSize = drawCanvasSize
                    )
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun Test() {
        TruVideoSdkTheme {
            val viewModel = remember {
                TruvideoSdkImageEditViewModel(
                    inputPath = "",
                    outputPath = "",
                    isPreviewMode = true
                )
            }
            Content(viewModel)
        }
    }
}

