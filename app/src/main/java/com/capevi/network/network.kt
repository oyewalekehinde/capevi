package com.capevi.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.IOException

fun downloadFile(
    url: String,
    destinationFile: File,
    onComplete: (Boolean) -> Unit,
) {
    val client = OkHttpClient()

    val request =
        Request
            .Builder()
            .url(url)
            .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        response.body?.let { body ->
            destinationFile.outputStream().use { fileOut ->
                fileOut.write(body.bytes())
                onComplete(true)
            }
        } ?: onComplete(false)
    }
}

suspend fun getAddressFromLocationIQ(
    latitude: Double,
    longitude: Double,
): String {
    val client = OkHttpClient()
    val url =
        "https://us1.locationiq.com/v1/reverse.php?key=pk.37f153909cb8e529678a9e83f4da73de&lat=$latitude&lon=$longitude&format=json"
    println(url)
    val request = Request.Builder().url(url).build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    // Parse JSON to get the address
                    val jsonObject = JSONObject(responseData)
                    jsonObject.getJSONObject("address").getString("town")
                } else {
                    "Address not found"
                }
            } else {
                "Failed to get address"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}
