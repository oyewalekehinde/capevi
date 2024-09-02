package com.capevi.shared.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

suspend fun uploadFileToFirebase(
    context: Context,
    file: File,
): String {
    val storageReference = FirebaseStorage.getInstance().reference.child("evidence/${getFileNameWithoutExtension(file.path)}")

    val uri = Uri.fromFile(file)
    val uploadTask = storageReference.putFile(uri)
    var imageUrl = ""
    try {
        val image = uploadTask.await()
        if (image.task.isSuccessful) {
            val downloadUrl = storageReference.downloadUrl.await()
            imageUrl = downloadUrl.toString()
            return imageUrl
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
    return imageUrl
}

fun getFileNameWithoutExtension(filePath: String): String {
    val file = File(filePath)
    val fileName = file.name
    val lastDotIndex = fileName.lastIndexOf('.')
    return if (lastDotIndex != -1) {
        fileName.substring(0, lastDotIndex)
    } else {
        fileName
    }
}

suspend fun uploadToFireStore(
    context: Context,
    dataBaseUrl: String,
    data: Map<String, Any>,
) {
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    try {
        firestore
            .collection(dataBaseUrl)
            .document()
            .set(
                data,
            ).await()
        Toast
            .makeText(
                context,
                "Evidence uploaded successfully",
                Toast.LENGTH_SHORT,
            ).show()
    } catch (e: Exception) {
        Toast
            .makeText(
                context,
                "Failed to save to database",
                Toast.LENGTH_SHORT,
            ).show()
    }
}
