package com.capevi.app.ui.case_composables

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ImagePreview(
    uri: Uri,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val inputStream = remember { context.contentResolver.openInputStream(uri) }
    val bitmap = remember { inputStream?.let { BitmapFactory.decodeStream(it) }?.asImageBitmap() }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image Preview",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .height(
                        100.dp,
                    ).width(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val mimeType = getMimeType(context, uri) ?: "application/octet-stream"
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
//                        val encodedJson =
//                            URLEncoder.encode(
//                                convertContentUriToFileUri(context, uri).toString(),
//                                StandardCharsets.UTF_8.toString(),
//                            )
//                        navController.navigate(Routes.MediaScreen.route + "/$encodedJson")
                    },
        )
    }
}
