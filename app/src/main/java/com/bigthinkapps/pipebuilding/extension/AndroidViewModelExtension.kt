package com.bigthinkapps.pipebuilding.extension

import androidx.lifecycle.AndroidViewModel
import com.bigthinkapps.pipebuilding.AppCore

fun AndroidViewModel.getString(resId: Int): String {
    return getApplication<AppCore>().getString(resId)
}