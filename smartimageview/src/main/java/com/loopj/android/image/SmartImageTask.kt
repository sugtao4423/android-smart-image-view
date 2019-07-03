package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message

class SmartImageTask(private val context: Context, private val image: SmartImage?) : Runnable {

    private var cancelled = false
    var onCompleteHandler: OnCompleteHandler? = null

    companion object {
        private const val BITMAP_READY = 0

        open class OnCompleteHandler : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg?.obj == null) {
                    return
                }
                onComplete(msg.obj as Bitmap)
            }

            open fun onComplete(bitmap: Bitmap?) {
            }
        }

        abstract class OnCompleteListener {
            abstract fun onComplete()

            /***
             * Convient method to get Bitmap after image is loaded.
             * Override this method to get handle of bitmap
             * Added overloaded implementation to make it backward compatible with
             * previous versions
             */
            fun onComplete(bitmap: Bitmap?) {
                onComplete()
            }
        }
    }

    override fun run() {
        if (image != null) {
            complete(image.getBitmap(context))
        }
    }

    fun cancel() {
        cancelled = true
    }

    private fun complete(bitmap: Bitmap?) {
        if (onCompleteHandler != null && !cancelled) {
            onCompleteHandler!!.sendMessage(onCompleteHandler!!.obtainMessage(BITMAP_READY, bitmap))
        }
    }

}