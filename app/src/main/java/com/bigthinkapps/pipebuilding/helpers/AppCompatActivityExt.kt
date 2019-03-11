package com.bigthinkapps.pipebuilding.helpers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.goToActivity(clazz: Class<*>) {
    val intent = Intent(this, clazz)
    this.startActivity(intent)
}