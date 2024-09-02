package com.capevi.app.ui
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

@Composable
fun MediaDisplayScreen(file: File) {
    val fileUri = file.toUri()
    val context = LocalContext.current

    if (isVideoFile(fileUri)) {
        VideoPlayerView(fileUri)
    } else if (isImageFile(fileUri)) {
        ZoomableImageView(fileUri)
    } else {
        // Handle other file types if needed
        Log.e("MediaDisplayScreen", "Unsupported file type")
    }
}

@Composable
fun ZoomableImageView(uri: Uri) {
    val bitmap = remember { BitmapFactory.decodeFile(uri.path)?.asImageBitmap() }

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    bitmap?.let {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                bitmap = it,
                contentDescription = "Zoomable Image",
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY,
                        ).pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale *= zoom
                                offsetX += pan.x
                                offsetY += pan.y
                            }
                        }.padding(16.dp),
            )
        }
    }
}

@Composable
fun VideoPlayerView(
    videoUri: Uri,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Initialize ExoPlayer
    val exoPlayer =
        remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(videoUri)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }

    // Cleanup the player when the view is destroyed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Create a PlayerView that is added to the Compose hierarchy
    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
            }
        },
    )
}

fun isImageFile(uri: Uri): Boolean {
    val mimeType = uri.toString().substringAfterLast('.', "")
    return mimeType in listOf("jpg", "jpeg", "png", "bmp", "gif", "webp")
}

fun isVideoFile(uri: Uri): Boolean {
    val mimeType = uri.toString().substringAfterLast('.', "")
    return mimeType in listOf("mp4", "mkv", "webm", "avi", "mov", "flv", "wmv")
}
