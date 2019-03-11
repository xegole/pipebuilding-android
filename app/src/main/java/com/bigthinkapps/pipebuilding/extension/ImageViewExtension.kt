package com.bigthinkapps.pipebuilding.extension

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.io.File

fun ImageView.imageByData(selectedImage: Uri) {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(selectedImage, filePathColumn, null, null, null)
    if (cursor != null) {
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn.first())
        val picturePath = cursor.getString(columnIndex)
        this.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        cursor.close()
    }
}

fun ImageView.setImageByFile(file: File) {
    Picasso.get().load(file).into(this)
}