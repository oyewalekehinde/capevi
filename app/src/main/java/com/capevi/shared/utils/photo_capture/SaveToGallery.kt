package com.capevi.shared.utils.photo_capture

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

class SavePhotoToGalleryUseCase(
    private val context: Context,
) {
    suspend fun call(capturePhotoBitmap: Bitmap): Result<Uri> =
        withContext(Dispatchers.IO) {
            val resolver: ContentResolver = context.applicationContext.contentResolver

            val imageCollection: Uri =
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY,
                        )
                    else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            // Publish a new image.
            val nowTimestamp: Long = System.currentTimeMillis()
            val imageContentValues: ContentValues =
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, UUID.randomUUID().toString() + ".jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.DATE_TAKEN, nowTimestamp)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/YourAppNameOrAnyOtherSubFolderName")
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        put(MediaStore.Images.Media.DATE_TAKEN, nowTimestamp)
                        put(MediaStore.Images.Media.DATE_ADDED, nowTimestamp)
                        put(MediaStore.Images.Media.DATE_MODIFIED, nowTimestamp)
                        put(MediaStore.Images.Media.AUTHOR, "Your Name")
                        put(MediaStore.Images.Media.DESCRIPTION, "Your description")
                    }
                }

            val imageMediaStoreUri: Uri? = resolver.insert(imageCollection, imageContentValues)

            // Write the image data to the new Uri.
            val result: Result<Uri> =
                imageMediaStoreUri?.let { uri ->
                    kotlin
                        .runCatching {
                            resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                                checkNotNull(outputStream) { "Couldn't create file for gallery, MediaStore output stream is null" }
                                capturePhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                imageContentValues.clear()
                                imageContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                                resolver.update(uri, imageContentValues, null, null)
                            }

                            Result.success(uri)
                        }.getOrElse { exception: Throwable ->
                            exception.message?.let(::println)
                            resolver.delete(uri, null, null)
                            Result.failure(exception)
                        }
                } ?: run {
                    Result.failure(Exception("Couldn't create file for gallery"))
                }

            return@withContext result
        }
}

class SaveVideoToGallery(
    private val context: Context,
) {
    suspend fun call(videoFile: File): Result<Uri> =
        withContext(Dispatchers.IO) {
            val nowTimestamp: Long = System.currentTimeMillis()
            val contentResolver = context.applicationContext.contentResolver
            val contentValues =
                ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, "Video name" + ".mp4")
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.MediaColumns.DATE_TAKEN, nowTimestamp)
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                    put(MediaStore.Images.Media.DATE_TAKEN, nowTimestamp)
                    put(MediaStore.Images.Media.DATE_ADDED, nowTimestamp)
                    put(MediaStore.Images.Media.DATE_MODIFIED, nowTimestamp)
                    put(MediaStore.Images.Media.AUTHOR, "Your Name")
                    put(MediaStore.Images.Media.DESCRIPTION, "Your description")
                }
            val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            val result: Result<Uri> =
                uri?.let {
                    kotlin
                        .runCatching {
                            contentResolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                                checkNotNull(outputStream) { "Couldn't create file for gallery, MediaStore output stream is null" }
                                videoFile.inputStream().copyTo(outputStream)

                                MediaScannerConnection.scanFile(context, arrayOf(videoFile.absolutePath), arrayOf("video/mp4"), null)
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                contentValues.clear()
                                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                                contentResolver.update(uri, contentValues, null, null)
                            }

                            Result.success(uri)
                        }.getOrElse { exception: Throwable ->
                            exception.message?.let(::println)
                            contentResolver.delete(uri, null, null)
                            Result.failure(exception)
                        }
                } ?: run {
                    Result.failure(Exception("Couldn't create file for gallery"))
                }

            return@withContext result
        }

//    val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//    uri?.let {
//        contentResolver.openOutputStream(it)?.use { outputStream ->
//            videoFile.inputStream().copyTo(outputStream)
//        }
//    }
//
//    // Notify the media scanner about the new video file
}

/**
 * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
 */
fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix =
        Matrix().apply {
            postRotate(-rotationDegrees.toFloat())
            postScale(1f, 1f)
        }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun saveBitmapToFile(
    context: Context,
    bitmap: Bitmap,
    filename: String,
): File {
    // Define the file path
    val directory = context.getExternalFilesDir(null)
    val file = File(directory, "$filename.png")

    // Write the bitmap to file
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }

    return file
}
