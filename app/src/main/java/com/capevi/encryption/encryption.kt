package com.capevi.encryption

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.capevi.shared.utils.getFileNameWithoutExtension
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

fun generateAndStoreKey(
    context: Context,
    keyAlias: String = "aes_key",
): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(256) // You can use 128 or 192 bits if needed
    val secretKey = keyGenerator.generateKey()

    val masterKeyAlias =
        MasterKey
            .Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    val encryptedSharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "secret_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
    encryptedSharedPreferences.edit().putString(keyAlias, encodedKey).apply()
    Log.d("SecretKey", encodedKey)
    println("Scretkey=====> $encodedKey")
    return secretKey
}

fun getStoredKey(
    context: Context,
    keyAlias: String = "aes_key",
): SecretKey? {
    val masterKeyAlias =
        MasterKey
            .Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    val encryptedSharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "secret_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    val encodedKey = encryptedSharedPreferences.getString(keyAlias, null) ?: return null
    val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)

    return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
}

fun readImageFile(
    context: Context,
    uri: Uri,
): ByteArray {
    val inputStream = context.contentResolver.openInputStream(uri)
    return inputStream?.readBytes() ?: ByteArray(0)
}

fun generateAESKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(256)
    return keyGenerator.generateKey()
}

fun encryptFileWithIv(
    context: Context,
    inputUri: Uri,
    outputFile: File,
    secretKey: SecretKey,
): File {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    // Generate a new IV for each encryption
    val iv = ByteArray(cipher.blockSize)
    Random.nextBytes(iv)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
    // Write the IV at the beginning of the output file
    FileOutputStream(outputFile).use { outputStream ->
        outputStream.write(iv) // Save IV at the start of the file
        context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                val encryptedBytes = cipher.update(buffer, 0, bytesRead)
                outputStream.write(encryptedBytes)
            }
            val finalBytes = cipher.doFinal()
            outputStream.write(finalBytes)
        } ?: throw Exception("Failed to open input stream from Uri")
    }
    // Return the encrypted file with IV prepended
    return outputFile
}

fun decryptFileWithIv(
    file: File,
    secretKey: SecretKey,
): File {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    // Create a temporary file to store the decrypted content
    val tempFile = File(file.parent, "${file.name}.temp")

    FileInputStream(file).use { inputStream ->
        // Extract the IV from the start of the file
        val iv = ByteArray(cipher.blockSize)
        inputStream.read(iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        FileOutputStream(tempFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                val decryptedBytes = cipher.update(buffer, 0, bytesRead)
                outputStream.write(decryptedBytes)
            }

            val finalBytes = cipher.doFinal()
            outputStream.write(finalBytes)
        }
    }

    // Replace the original file with the decrypted file
    if (file.delete()) {
        tempFile.renameTo(file)
    } else {
        throw Exception("Failed to delete the original file")
    }

    // Return the file with decrypted content
    return file
}

fun saveListOfEncryptedFiles(
    context: Context,
    dataList: List<Any>,
): List<Any> {
    val secretKey = EncryptionKeyManager.getKey()
    val encryptedFiles: MutableList<Any> = mutableListOf()
    var file: Any
    dataList.forEachIndexed { _, uri ->
        if (uri is Uri) {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            val outputFile = File(context.filesDir, "${getFileNameWithoutExtension(uri.path!!)}.$extension.aes")
            file = encryptFileWithIv(context, uri, outputFile, secretKey!!)
        } else {
            file = uri
        }
        encryptedFiles.add(file)
    }
    return encryptedFiles
}

object EncryptionKeyManager {
    private var secretKey: SecretKey? = null

    fun initKey(context: Context) {
        secretKey = getStoredKey(context) ?: generateAndStoreKey(context)
    }

// TODO remeber to upload this key to firestore so they can fetch the key for the app
    fun getKey(): SecretKey? =
        SecretKeySpec(
            Base64.decode("g0NptcN4FkZpDRGYtMB3z7sV+7N8af26pushssdkhq0=", Base64.DEFAULT),
            0,
            Base64.decode("g0NptcN4FkZpDRGYtMB3z7sV+7N8af26pushssdkhq0=", Base64.DEFAULT).size,
            "AES",
        )
}
