package com.capevi.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capevi.shared.utils.getFileNameWithoutExtension
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel
    @Inject
    constructor(
//        private val repository: MyRepository,
    ) : ViewModel() {
        private val _files = MutableStateFlow<MutableList<String>>(mutableListOf())
        val files: StateFlow<List<String>> get() = _files
        private val _storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private val _uploadFileState = MutableLiveData<UploadFileState>()
        val uploadFileState: LiveData<UploadFileState> = _uploadFileState

        fun resetFileState()  {
            _uploadFileState.value = UploadFileState.InitalState
        }

        fun uploadFile(
            folderName: String = "evidence",
            files: List<String>,
        ) {
            var filesUploaded = 0
            _uploadFileState.value = UploadFileState.UploadingFile

            try {
                val imageFiles = files.toMutableList()
                imageFiles.removeAll { it.isEmpty() }

                val downloadLink: MutableList<String> = mutableListOf()

                for (file in imageFiles) {
                    var imageUrl = ""
                    if (file.contains("https:")) {
                        imageUrl = file
                        downloadLink.add(imageUrl)
                        filesUploaded++
                        if (filesUploaded == imageFiles.size) {
                            _uploadFileState.value = UploadFileState.UploadedFile(downloadLink)
                        }
                    } else {
                        val fileReference =
                            _storageReference.child("$folderName/${getFileNameWithoutExtension(file)}.aes")
                        val uri = Uri.fromFile(File(file))
                        val uploadTask = fileReference.putFile(uri)
                        uploadTask
                            .addOnSuccessListener { taskSnapshot ->
                                // Handle success
                                taskSnapshot.storage.downloadUrl
                                    .addOnSuccessListener { uri ->
                                        val downloadUrl = uri
                                        imageUrl = downloadUrl.toString()
                                        downloadLink.add(imageUrl)
                                        filesUploaded++
                                        if (filesUploaded == imageFiles.size) {
                                            _uploadFileState.value = UploadFileState.UploadedFile(downloadLink)
                                        }
                                    }
                            }.addOnFailureListener { exception ->
                                throw Exception(exception)
                            }
                    }
                }
            } catch (error: Exception) {
                _uploadFileState.value = UploadFileState.UploadedError(error?.message ?: "something went wrong")
            }
        }
    }

sealed class UploadFileState {
    data object InitalState : UploadFileState()

    data object UploadingFile : UploadFileState()

    data class UploadedFile(
        val downLoadUrls: List<String>,
    ) : UploadFileState()

    data class UploadedError(
        val message: String,
    ) : UploadFileState()
}
