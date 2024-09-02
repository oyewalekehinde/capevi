// package com.capevi.shared.utils.photo_capture
// import android.Manifest
// import android.content.Context
// import android.net.Uri
// import android.os.Bundle
// import android.util.Log
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.camera.core.*
// import androidx.camera.lifecycle.ProcessCameraProvider
// import androidx.camera.video.*
// import androidx.camera.view.PreviewView
// import androidx.compose.foundation.layout.*
// import androidx.compose.material3.Button
// import androidx.compose.material3.Text
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.viewinterop.AndroidView
// import androidx.core.content.ContextCompat
// import java.io.File
// import java.text.SimpleDateFormat
// import java.util.*
//
// class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            CameraXApp()
//        }
//    }
// }
//
// @Composable
// fun CameraXApp() {
//    val context = LocalContext.current
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    var videoUri by remember { mutableStateOf<Uri?>(null) }
//    var recording: Recording? by remember { mutableStateOf(null) }
//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//
//    // Request camera permission
//    val cameraPermissionLauncher =
//        rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission(),
//            onResult = { granted ->
//                if (granted) {
//                    cameraProviderFuture.addListener({
//                        val cameraProvider = cameraProviderFuture.get()
//                        startCamera(context, cameraProvider) { imageCapture, videoCapture, previewView ->
//                            CameraPreview(imageCapture, videoCapture, previewView, recording) { imgUri, vidUri, rec ->
//                                imageUri = imgUri
//                                videoUri = vidUri
//                                recording = rec
//                            }
//                        }
//                    }, ContextCompat.getMainExecutor(context))
//                }
//            },
//        )
//
//    LaunchedEffect(Unit) {
//        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//    }
//
//    Column {
//        if (imageUri != null) {
//            Text(text = "Image captured: $imageUri")
//        }
//        if (videoUri != null) {
//            Text(text = "Video recorded: $videoUri")
//        }
//    }
// }
//
// @Composable
// fun CameraPreview(
//    imageCapture: ImageCapture,
//    videoCapture: VideoCapture<Recorder>,
//    previewView: PreviewView,
//    recording: Recording?,
//    onCapture: (Uri?, Uri?, Recording?) -> Unit,
// ) {
//    val context = LocalContext.current
//
//    Column {
//        Box(modifier = Modifier.fillMaxSize()) {
//            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
//
//            Row(
//                modifier =
//                    Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp),
//            ) {
//                Button(onClick = {
//                    val photoFile = createFile(context, "IMG", ".jpg")
//                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//                    imageCapture.takePicture(
//                        outputOptions,
//                        ContextCompat.getMainExecutor(context),
//                        object : ImageCapture.OnImageSavedCallback {
//                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                                onCapture(Uri.fromFile(photoFile), null, recording)
//                            }
//
//                            override fun onError(exception: ImageCaptureException) {
//                                Log.e("CameraXApp", "Image capture failed: ${exception.message}")
//                            }
//                        },
//                    )
//                }) {
//                    Text(text = "Capture Image")
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Button(onClick = {
//                    if (recording != null) {
//                        // Stop the recording
//                        recording?.stop()
//                        onCapture(null, null, null)
//                    } else {
//                        val videoFile = createFile(context, "VID", ".mp4")
//                        val mediaStoreOutputOptions = FileOutputOptions.Builder(videoFile).build()
//                        val recorder = videoCapture.output
//
//                        val newRecording =
//                            recorder
//                                .prepareRecording(context, mediaStoreOutputOptions)
//                                .withAudioEnabled()
//                                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
//                                    when (recordEvent) {
//                                        is VideoRecordEvent.Start -> {
//                                            onCapture(null, null, newRecording)
//                                        }
//                                        is VideoRecordEvent.Finalize -> {
//                                            if (!recordEvent.hasError()) {
//                                                onCapture(null, Uri.fromFile(videoFile), null)
//                                            } else {
//                                                Log.e("CameraXApp", "Video recording failed: ${recordEvent.error}")
//                                            }
//                                        }
//                                    }
//                                }
//                    }
//                }) {
//                    Text(text = if (recording != null) "Stop Recording" else "Record Video")
//                }
//            }
//        }
//    }
// }
//
// private fun createFile(
//    context: Context,
//    prefix: String,
//    suffix: String,
// ): File {
//    val directory = context.getExternalFilesDir(null)
//    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
//    return File(directory, "${prefix}_${fileName}$suffix")
// }
//
// private fun startCamera(
//    context: Context,
//    cameraProvider: ProcessCameraProvider,
//    onCameraReady: (ImageCapture, VideoCapture<Recorder>, PreviewView) -> Unit,
// ) {
//    val previewView =
//        PreviewView(context).apply {
//            layoutParams =
//                ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                )
//        }
//
//    val preview =
//        Preview.Builder().build().also {
//            it.setSurfaceProvider(previewView.surfaceProvider)
//        }
//
//    val imageCapture = ImageCapture.Builder().build()
//
//    val recorder =
//        Recorder
//            .Builder()
//            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
//            .build()
//
//    val videoCapture = VideoCapture.withOutput(recorder)
//
//    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//    try {
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(
//            context as ComponentActivity,
//            cameraSelector,
//            preview,
//            imageCapture,
//            videoCapture,
//        )
//        onCameraReady(imageCapture, videoCapture, previewView)
//    } catch (exc: Exception) {
//        Log.e("CameraXApp", "Camera binding failed", exc)
//    }
// }
