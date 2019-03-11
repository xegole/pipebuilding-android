package com.bigthinkapps.pipebuilding.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}