package com.loopj.android.image

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView
import java.util.concurrent.Executors

class SmartImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    companion object {
        private const val LOADING_THREADS = 4
        private var threadPool = Executors.newFixedThreadPool(LOADING_THREADS)

        fun cancelAllTasks() {
            threadPool.shutdownNow()
            threadPool = Executors.newFixedThreadPool(LOADING_THREADS)
        }
    }

    private var currentTask: SmartImageTask? = null

    // Helpers to set image by URL
    @JvmOverloads
    fun setImageUrl(url: String?, fallbackResource: Int? = null, loadingResource: Int? = null, completeListener: SmartImageTask.Companion.OnCompleteListener? = null) {
        setImage(WebImage(url), fallbackResource, loadingResource, completeListener)
    }

    // Helpers to set image by contact address book id
    @JvmOverloads
    fun setImageContact(contactId: Long, fallbackResource: Int? = null, loadingResource: Int? = null) {
        setImage(ContactImage(contactId), fallbackResource, loadingResource)
    }

    // Set image using SmartImage object
    @JvmOverloads
    fun setImage(image: SmartImage, completeListener: SmartImageTask.Companion.OnCompleteListener? = null) {
        setImage(image, null, null, completeListener)
    }

    @JvmOverloads
    fun setImage(image: SmartImage, fallbackResource: Int?, completeListener: SmartImageTask.Companion.OnCompleteListener? = null) {
        setImage(image, fallbackResource, fallbackResource, completeListener)
    }

    fun setImage(image: SmartImage, fallbackResource: Int?, loadingResource: Int?) {
        setImage(image, fallbackResource, loadingResource, null)
    }

    fun setImage(image: SmartImage, fallbackResource: Int?, loadingResource: Int?, completeListener: SmartImageTask.Companion.OnCompleteListener?) {
        // Set a loading resource
        if (loadingResource != null) {
            setImageResource(loadingResource)
        }

        // Cancel any existing tasks for this image view
        currentTask?.cancel()
        currentTask = null

        // Set up the new task
        currentTask = SmartImageTask(context, image)
        currentTask!!.onCompleteHandler = object : SmartImageTask.Companion.OnCompleteHandler() {

            override fun onComplete(bitmap: Bitmap?) {
                if (bitmap != null) {
                    setImageBitmap(bitmap)
                } else {
                    // Set fallback resource
                    if (fallbackResource != null) {
                        setImageResource(fallbackResource)
                    }
                }

                completeListener?.onComplete(bitmap)
            }
        }
        // Run the task in a threadpool
        threadPool.execute(currentTask)
    }

}