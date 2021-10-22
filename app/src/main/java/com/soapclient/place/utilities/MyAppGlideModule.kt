package com.soapclient.place.utilities

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

@GlideModule
class MyAppGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(StorageReference::class.java, InputStream::class.java, FirebaseImageLoader.Factory())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(
            LruResourceCache(
                MemorySizeCalculator.Builder(context)
                    .setMemoryCacheScreens(2f)
                    .build()
                    .memoryCacheSize.toLong()
            )
        )
        builder.setBitmapPool(
            LruBitmapPool(
                MemorySizeCalculator.Builder(context)
                    .setBitmapPoolScreens(3f)
                    .build()
                    .bitmapPoolSize.toLong()
            )
        )
        builder.setDiskCache(InternalCacheDiskCacheFactory(context))
    }
}