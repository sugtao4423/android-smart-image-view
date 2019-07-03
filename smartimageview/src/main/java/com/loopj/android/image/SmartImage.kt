package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap

interface SmartImage {
    fun getBitmap(context: Context): Bitmap?
}