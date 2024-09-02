package com.capevi.app.ui.case_composables

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.capevi.navigation.Routes
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MediaPreview(
    uri: Uri,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val mimeType = getMimeType(context, uri) ?: "application/octet-stream"
    if (isVideoMimeType(mimeType)) {
        // Load and display video thumbnail
        val thumbnailFile = remember { File(context.cacheDir, "thumbnail_${uri.hashCode()}.png") }

        LaunchedEffect(uri) {
            val bitmap = getVideoThumbnailFromUri(context, uri)
            bitmap?.let {
                saveBitmapToFileHome(it, thumbnailFile)
            }
        }
        val imageBitmap =
            remember {
                if (thumbnailFile.exists()) {
                    BitmapFactory.decodeFile(thumbnailFile.absolutePath)?.asImageBitmap()
                } else {
                    null
                }
            }

        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .height(
                            100.dp,
                        ).width(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
//                            val mimeType = getMimeType(context, uri) ?: "application/octet-stream"
//                            val intent =
//                                Intent(Intent.ACTION_VIEW).apply {
//                                    setDataAndType(uri, mimeType) // e.g., "image/jpeg" or "video/mp4"
//                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                                }
//                            if (intent.resolveActivity(context.packageManager) != null) {
//                                context.startActivity(intent)
//                            } else {
//                                // Handle the case where no app can open the file
//                                Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
//                            }
                            val encodedJson =
                                URLEncoder.encode(
                                    convertContentUriToFileUri(context, uri).toString(),
                                    StandardCharsets.UTF_8.toString(),
                                )
                            navController.navigate(Routes.MediaScreen.route + "/$encodedJson")
                        },
            )
        }
            ?: Box(
                modifier =
                    Modifier
                        .height(
                            100.dp,
                        ).width(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = getIconForMimeType(mimeType),
                    contentDescription = "Video Icon",
                    modifier =
                        Modifier.size(64.dp).clickable {
                            Log.e("URITEXT", mimeType)
                            val intent =
                                Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, mimeType) // e.g., "image/jpeg" or "video/mp4"
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No application found to open PDF", Toast.LENGTH_SHORT).show()
                            }

//                            Log.d("URI Content", uri)

//                            val encodedJson =
//                                URLEncoder.encode(
//                                    convertContentUriToFileUri(context, uri).toString(),
//                                    StandardCharsets.UTF_8.toString(),
//                                )
//                            navController.navigate(Routes.MediaScreen.route + "/$encodedJson")
                        },
                    tint = androidx.compose.ui.graphics.Color.Gray,
                )
            }
    } else if (isImageMimeType(mimeType)) {
        // Load and display image directly
        ImagePreview(uri, navController)
    } else {
        Box(
            modifier =
                Modifier
                    .height(
                        100.dp,
                    ).width(100.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = getIconForMimeType(mimeType),
                contentDescription = "Video Icon",
                modifier =
                    Modifier.size(64.dp).clickable {
                        Log.e("URITEXT", mimeType)
                        val intent =
                            Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, mimeType) // e.g., "image/jpeg" or "video/mp4"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No application found to open PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                tint = androidx.compose.ui.graphics.Color.Gray,
            )
        }
    }
}

fun getIconForMimeType(mimeType: String): ImageVector =
    when (mimeType) {
        "audio/mpeg" -> Icons.Filled.SurroundSound // MP3
        "audio/wav" -> Icons.Filled.SurroundSound // WAV
        "audio/ogg" -> Icons.Filled.SurroundSound // OGG
        "video/3gpp" -> Icons.Filled.SurroundSound // 3GPP
        "audio/flac" -> Icons.Filled.SurroundSound // FLAC
        "video/mp4" -> Icons.Filled.VideoFile // MP4
        "video/avi" -> Icons.Filled.VideoFile // AVI
        "application/pdf" -> Icons.Filled.PictureAsPdf // PDF
        else -> Icons.Filled.FilePresent // Default icon
    }
