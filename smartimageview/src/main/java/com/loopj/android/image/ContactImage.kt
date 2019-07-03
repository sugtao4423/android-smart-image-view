package com.loopj.android.image

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract

class ContactImage(private val contactId: Long) : SmartImage {

    override fun getBitmap(context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        val contentResolver = context.contentResolver

        try {
            val uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
            val input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri)
            if (input != null) {
                bitmap = BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

}