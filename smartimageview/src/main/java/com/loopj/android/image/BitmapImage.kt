package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap

class BitmapImage(private val bitmap: Bitmap?) : SmartImage {

    override fun getBitmap(context: Context): Bitmap? {
        return bitmap
    }

}