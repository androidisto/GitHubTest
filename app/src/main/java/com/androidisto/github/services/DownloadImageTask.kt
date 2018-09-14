package com.androidisto.github.services

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.ImageView


// class used to cache image and check cache memory for already cached images.


class DownloadImageTask {


    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // Use 1/8th of the available memory for this memory cache.
    val cacheSize = maxMemory / 8

    var mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }
    }


    fun loadBitmap(avatarURL: String, imageView: ImageView) {

        val bitmap = getBitmapFromMemCache(avatarURL)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)

            ImageDownloaderTask(imageView).execute(avatarURL)
        }
    }

    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return mMemoryCache.get(key)
    }
}