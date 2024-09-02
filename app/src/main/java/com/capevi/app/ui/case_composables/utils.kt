package com.capevi.app.ui.case_composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import androidx.compose.ui.graphics.Color
import com.capevi.app.ui.theme.ColorClosed
import com.capevi.app.ui.theme.ColorInvestigating
import com.capevi.app.ui.theme.ColorOpened
import com.capevi.app.ui.theme.ColorResolved
import com.capevi.app.ui.theme.ColorReviewed
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun convertContentUriToFileUri(
    context: Context,
    contentUri: Uri,
): Uri? {
    // Create a temporary file
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(contentUri)
    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    val tempFile = File.createTempFile("${getFileNameFromContentUri(context, contentUri)}", ".$extension", context.cacheDir)

    try {
        // Open the input stream from the content URI
        context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
            // Create an output stream to write the content to the temporary file
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        // Return the file URI
        return Uri.fromFile(tempFile)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getFileNameFromContentUri(
    context: Context,
    uri: Uri,
): String? {
    val cursor =
        context.contentResolver.query(
            uri,
            arrayOf(android.provider.MediaStore.Images.Media.DISPLAY_NAME),
            null,
            null,
            null,
        )

    return cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.MediaStore.Images.Media.DISPLAY_NAME)
            it.getString(nameIndex)
        } else {
            null
        }
    }
}

fun getMimeType(
    context: Context,
    uri: Uri,
): String? {
    val contentResolver = context.contentResolver
    return contentResolver.getType(uri)
}

fun isVideoMimeType(mimeType: String?): Boolean = mimeType?.startsWith("video/") == true

fun isImageMimeType(mimeType: String?): Boolean = mimeType?.startsWith("image/") == true

fun getVideoThumbnailFromUri(
    context: Context,
    videoUri: Uri,
): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(videoUri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        retriever.setDataSource(fileDescriptor)
        retriever.getFrameAtTime(0)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}

fun saveBitmapToFileHome(
    bitmap: Bitmap,
    outputFile: File,
) {
    try {
        FileOutputStream(outputFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

suspend fun downloadFileFromFirebase(
    fileUrl: String,
    localFile: File,
): Result<File> =
    try {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)

        // Download the file
        val result =
            suspendCoroutine<Result<File>> { continuation ->
                storageReference
                    .getFile(localFile)
                    .addOnSuccessListener {
                        // File downloaded and saved successfully
                        continuation.resume(Result.success(localFile))
                    }.addOnFailureListener { exception ->
                        // Handle errors
                        continuation.resume(Result.failure(exception))
                    }
            }

        result
    } catch (e: Exception) {
        Result.failure(e)
    }

fun loadBitmapFromFile(file: File): Bitmap? = BitmapFactory.decodeFile(file.absolutePath)

fun getColorForStatusText(status: String): Color =
    when (status) {
        "Opened" -> ColorOpened
        "Investigating" -> ColorInvestigating
        "Reviewed" -> ColorReviewed
        "Resolved" -> ColorResolved
        "Closed" -> ColorClosed
        else -> Color.Gray // Default color
    }

fun getColorForStatusContainer(status: String): Color =
    when (status) {
        "Opened" -> ColorOpened.copy(alpha = 0.5f)
        "Investigating" -> ColorInvestigating.copy(alpha = 0.5f)
        "Reviewed" -> ColorReviewed.copy(alpha = 0.5f)
        "Resolved" -> ColorResolved.copy(alpha = 0.5f)
        "Closed" -> ColorClosed.copy(alpha = 0.5f)
        else -> Color.Gray.copy(alpha = 0.5f) // Default color
    }

fun isFileCached(
    context: Context,
    fileName: String,
): Boolean {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, fileName)
    return file.exists()
}
