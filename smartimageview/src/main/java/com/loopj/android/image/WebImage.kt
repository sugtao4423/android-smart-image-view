package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.net.URL

class WebImage(private val url: String?) : SmartImage {

    companion object {
        private const val CONNECT_TIMEOUT = 5000
        private const val READ_TIMEOUT = 10000

        private var webImageCache: WebImageCache? = null

        fun removeFromCache(url: String) {
            webImageCache?.remove(url)
        }
    }

    override fun getBitmap(context: Context): Bitmap? {
        // Don't leak context
        if (webImageCache == null) {
            webImageCache = WebImageCache(context)
        }

        // Try getting bitmap from cache first
        var bitmap: Bitmap? = null
        if (url != null) {
            bitmap = webImageCache!!.get(url)
            if (bitmap == null) {
                bitmap = getBitmapFromUrl(url)
                if (bitmap != null) {
                    webImageCache!!.put(url, bitmap)
                }
            }
        }
        return bitmap
    }

    private fun getBitmapFromUrl(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val conn = URL(url).openConnection().apply {
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
            }
            bitmap = BitmapFactory.decodeStream(conn.content as InputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

}