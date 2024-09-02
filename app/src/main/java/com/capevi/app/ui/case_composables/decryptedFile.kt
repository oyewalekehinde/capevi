package com.capevi.app.ui.case_composables

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Neutral
import com.capevi.encryption.decryptFileWithIv
import com.capevi.shared.utils.getFileNameWithoutExtension
import com.capevi.viewmodels.CaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.crypto.SecretKey

@Composable
fun DecryptedImageFromUrl(
    url: String,
    secretKey: SecretKey,
    contentScale: ContentScale = ContentScale.Crop,
    navController: NavHostController,
    caseViewModel: CaseViewModel,
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    LaunchedEffect(Unit) {
        if (isFileCached(context, getFileNameWithoutExtension(url))) {
            val cachedFile = File(context.cacheDir, getFileNameWithoutExtension(url))
            imageFile = cachedFile
            imageBitmap = loadBitmapFromFile(cachedFile)?.asImageBitmap()
        } else {
            val destinationFile = File(context.cacheDir, getFileNameWithoutExtension(url))
            CoroutineScope(Dispatchers.IO).launch {
                val result = downloadFileFromFirebase(url, destinationFile)

                result
                    .onSuccess { file ->
                        // Handle success, file is saved locally
                        println("File downloaded to: ${file.absolutePath}")
                        val decryptedFile = decryptFileWithIv(file, secretKey)
                        imageFile = file
                        imageBitmap = loadBitmapFromFile(decryptedFile)?.asImageBitmap()
                    }.onFailure { exception ->
                        // Handle failure
                        println("Failed to download file: ${exception.message}")
                    }
            }
        }
        val fileList = caseViewModel.caseEvidenceFileList.toMutableList()
//        fileList.add(imageFile?.path!!)
        caseViewModel.caseEvidenceFileList = fileList
    }
    var uri: Uri?
    var mimeType: String = ""
    imageFile?.let {
        uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile!!)
        mimeType = getMimeType(context, uri!!) ?: "application/octet-stream"
    }
    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier =
                Modifier
                    .height(
                        100.dp,
                    ).width(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile!!)
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
//                        val fileUri = Uri.fromFile(imageFile)
//                        val encodedJson = URLEncoder.encode(fileUri.toString(), StandardCharsets.UTF_8.toString())
//
//                        navController.navigate(Routes.MediaScreen.route + "/$encodedJson")
                    },
            contentScale = contentScale,
        )
    }
        ?: Box(
            Modifier
                .height(
                    100.dp,
                ).width(100.dp)
                .background(Neutral[50]!!, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = getIconForMimeType(mimeType),
                contentDescription = "Video Icon",
                modifier =
                    Modifier.size(64.dp).clickable {
                        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile!!)
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
//                        val fileUri = Uri.fromFile(imageFile)
//                        val encodedJson = URLEncoder.encode(fileUri.toString(), StandardCharsets.UTF_8.toString())
//                        navController.navigate(Routes.MediaScreen.route + "/$encodedJson")
                    },
                tint = androidx.compose.ui.graphics.Color.Gray,
            )
        }
}
