package com.capevi.shared.utils.photo_capture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Neutral
import com.capevi.viewmodels.SharedViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

@Composable
fun cameraScreen(
    navController: NavHostController,
    viewModel: SharedViewModel,
    isVideo: Boolean,
) {
    cameraContent(
        navController,
        viewModel,
        isVideo,
    )
}

@SuppressLint("MissingPermission")
@Composable
private fun cameraContent(
    navController: NavHostController,
    viewModel: SharedViewModel,
    isVideo: Boolean,
) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE,
                )
            }
        }
    var recording: Recording? by remember { mutableStateOf(null) }
    var timer by remember { mutableStateOf(0L) }
    var isRecording by remember { mutableStateOf(false) }

    // Launched effect to update timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                timer++
                kotlinx.coroutines.delay(1000L)
            }
        } else {
            timer = 0L
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Box(
                modifier =
                    Modifier.fillMaxWidth(1f).padding(start = 25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .height(60.dp)
                            .width(60.dp)
                            .border(3.5.dp, color = androidx.compose.ui.graphics.Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .height(
                                    if (isRecording) 25.dp else 50.dp,
                                ).width(
                                    if (isRecording) 25.dp else 50.dp,
                                ).background(
                                    if (isVideo) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.White,
                                    shape = if (isRecording) RoundedCornerShape(8.dp) else CircleShape,
                                ).clickable
                                {
                                    if (!isVideo) {
                                        capturePhoto(context, cameraController, viewModel)
                                    } else {
                                        if (isRecording) {
                                            if (recording != null) {
                                                recording?.stop()
                                                recording = null
                                            }
                                        } else {
                                            val outputFile = createFile(context, "VID", ".mp4")

                                            recording =
                                                cameraController.startRecording(
                                                    FileOutputOptions.Builder(outputFile).build(),
                                                    AudioConfig.create(true),
                                                    ContextCompat.getMainExecutor(context),
                                                ) { event ->
                                                    when (event) {
                                                        is VideoRecordEvent.Start -> {
                                                            isRecording = true
                                                        }
                                                        is VideoRecordEvent.Finalize -> {
                                                            isRecording = false
                                                            if (event.hasError()) {
                                                                recording?.close()
                                                                recording = null
                                                            } else {
                                                                runBlocking {
                                                                    val data = SaveVideoToGallery(context).call(outputFile)
                                                                    data.onSuccess { uri ->
                                                                        run {
                                                                            viewModel.addMedia(uri)

                                                                            Toast
                                                                                .makeText(
                                                                                    context,
                                                                                    "Evidence captured and saved to gallery",
                                                                                    Toast.LENGTH_SHORT,
                                                                                ).show()
                                                                        }
                                                                    }
                                                                    data.onFailure { error ->
                                                                        Toast
                                                                            .makeText(
                                                                                context,
                                                                                error.message,
                                                                                Toast.LENGTH_SHORT,
                                                                            ).show()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                },
                    ) {
                    }
                }
            }
        },
    ) { paddingValues: PaddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                factory = { context ->
                    PreviewView(context)
                        .apply {
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            setBackgroundColor(Color.BLACK)
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FILL_START
                        }.also { previewView ->
                            previewView.controller = cameraController
                            cameraController.bindToLifecycle(lifecycleOwner)
                        }
                },
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .background(
                            color = androidx.compose.ui.graphics.Color.White,
                        ).height(30.dp)
                        .fillMaxWidth(1f)
                        .padding(horizontal = 20.dp),
            ) {
                Text(
                    modifier = Modifier.clickable { navController.popBackStack() },
                    text = "Done",
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Black[950]!!,
                            fontWeight = FontWeight.W500,
                            textAlign = TextAlign.Center,
                        ),
                )
                if (isVideo) {
                    Text(
                        text = formatTimeForVideo(timer),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Neutral[950]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    viewModel: SharedViewModel,
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(
        mainExecutor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val correctedBitmap: Bitmap =
                    image
                        .toBitmap()
                        .rotateBitmap(image.imageInfo.rotationDegrees)
                runBlocking {
                    val data = SavePhotoToGalleryUseCase(context).call(correctedBitmap)
                    data.onSuccess { uri ->
                        run {
                            viewModel.addMedia(uri)

                            Toast
                                .makeText(
                                    context,
                                    "Evidence captured and saved to gallery",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }
                    data.onFailure { error ->
                        Toast
                            .makeText(
                                context,
                                error.message,
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }

                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraContent", "Error capturing image", exception)
            }
        },
    )
}

@SuppressLint("MissingPermission")
private fun recordVideo(
    controller: LifecycleCameraController,
    context: Context,
    recording: Recording,
) {
}

fun createFile(
    context: Context,
    prefix: String,
    suffix: String,
): File {
    val directory = context.getExternalFilesDir(null)
    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    return File(directory, "${prefix}_${fileName}$suffix")
}

private fun formatTimeForVideo(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
