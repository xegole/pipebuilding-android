package com.bigthinkapps.pipebuilding.extension

import android.graphics.Bitmap
import android.view.View

fun View.getBitmapScreen(callback: (Bitmap) -> Unit) {
    try {
        val bitmap: Bitmap
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        val src = this.drawingCache
        bitmap = Bitmap.createBitmap(src!!, 0, 0, src.width, src.height)
        this.destroyDrawingCache()
        this.isDrawingCacheEnabled = false
        src.recycle()
        callback(bitmap)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}