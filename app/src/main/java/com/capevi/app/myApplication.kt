package com.capevi.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.capevi.encryption.EncryptionKeyManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication :
    Application(),
    ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        EncryptionKeyManager.initKey(this)
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader(this)
            .newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache
                    .Builder(this)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }.diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache
                    .Builder()
                    .maxSizePercent(0.03)
                    .directory(cacheDir)
                    .build()
            }.logger(DebugLogger())
            .build()
}
