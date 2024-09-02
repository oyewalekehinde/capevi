package com.capevi.encryption
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    return EncryptedSharedPreferences.create(
        "user_credentials",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
}

fun saveCredentials(
    context: Context,
    username: String,
    password: String,
) {
    val sharedPreferences = getEncryptedSharedPreferences(context)
    with(sharedPreferences.edit()) {
        putString("email", username)
        putString("password", password)
        apply()
    }
}

fun getCredentials(context: Context): Pair<String?, String?> {
    val sharedPreferences = getEncryptedSharedPreferences(context)
    val username = sharedPreferences.getString("email", null)
    val password = sharedPreferences.getString("password", null)
    return Pair(username, password)
}

fun deleteCredentials(context: Context) {
    val sharedPreferences = getEncryptedSharedPreferences(context)
    with(sharedPreferences.edit()) {
        remove("email")
        remove("password")
        apply()
    }
}
