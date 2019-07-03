package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class WebImageCache(context: Context) {

    companion object {
        private const val DISK_CACHE_PATH = "/web_image_cache/"
    }

    // Set up in-memory cache store
    private val memoryCache = ConcurrentHashMap<String, SoftReference<Bitmap>>()

    // Set up disk cache store
    private val diskCachePath = context.applicationContext.cacheDir.absolutePath + DISK_CACHE_PATH

    // Set up threadpool for image fetching tasks
    private val writeThread = Executors.newSingleThreadExecutor()

    private var diskCacheEnabled = false

    init {
        val outFile = File(diskCachePath)
        outFile.mkdirs()
        diskCacheEnabled = outFile.exists()
    }

    fun get(url: String): Bitmap? {
        // Check for image in memory
        var bitmap = getBitmapFromMemory(url)

        // Check for image on disk cache
        if (bitmap == null) {
            bitmap = getBitmapFromDisk(url)

            // Write bitmap back into memory cache
            if (bitmap != null) {
                cacheBitmapToMemory(url, bitmap)
            }
        }
        return bitmap
    }

    fun put(url: String, bitmap: Bitmap) {
        cacheBitmapToMemory(url, bitmap)
        cacheBitmapToDisk(url, bitmap)
    }

    fun remove(url: String) {
        // Remove from memory cache
        memoryCache.remove(getCacheKey(url))

        // Remove from file cache
        File(diskCachePath, getCacheKey(url)).let {
            if (it.exists() && it.isFile) {
                it.delete()
            }
        }
    }

    fun clear() {
        // Remove everything from memory cache
        memoryCache.clear()

        // Remove everything from file cache
        val cachedFileDir = File(diskCachePath)
        if (cachedFileDir.exists() && cachedFileDir.isDirectory) {
            cachedFileDir.listFiles().map {
                if (it.exists() && it.isFile) {
                    it.delete()
                }
            }
        }
    }

    fun getCacheSize(): Long {
        var fileSize = 0L
        val cachedFileDir = File(diskCachePath)
        if (cachedFileDir.exists() && cachedFileDir.isDirectory) {
            cachedFileDir.listFiles().map {
                if (it.exists() && it.isFile) {
                    fileSize += it.length()
                }
            }
        }
        return fileSize
    }

    private fun cacheBitmapToMemory(url: String, bitmap: Bitmap) {
        memoryCache[getCacheKey(url)] = SoftReference(bitmap)
    }

    private fun cacheBitmapToDisk(url: String, bitmap: Bitmap) {
        writeThread.execute {
            if (diskCacheEnabled) {
                var ostream: BufferedOutputStream? = null
                try {
                    ostream = BufferedOutputStream(FileOutputStream(File(diskCachePath, getCacheKey(url))), 2 * 1024)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } finally {
                    try {
                        ostream?.flush()
                        ostream?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }
    }

    private fun getBitmapFromMemory(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        val softRef = memoryCache[getCacheKey(url)]
        if (softRef != null) {
            bitmap = softRef.get()
        }
        return bitmap
    }

    private fun getBitmapFromDisk(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        if (diskCacheEnabled) {
            val filePath = getFilePath(url)
            val file = File(filePath)
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath)
            }
        }
        return bitmap
    }

    private fun getFilePath(url: String): String {
        return diskCachePath + getCacheKey(url)
    }

    private fun getCacheKey(url: String): String {
        return url.replace(Regex("[.:/,%?&=]"), "+").replace(Regex("[+]+"), "+")
    }

}