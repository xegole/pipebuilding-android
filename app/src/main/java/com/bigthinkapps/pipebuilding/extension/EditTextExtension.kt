package com.bigthinkapps.pipebuilding.extension

import androidx.appcompat.widget.AppCompatEditText

fun AppCompatEditText.getInt(): Int {
    val data = this.text.toString()
    return if (data.isEmpty()) {
        0
    } else {
        data.toInt()
    }
}