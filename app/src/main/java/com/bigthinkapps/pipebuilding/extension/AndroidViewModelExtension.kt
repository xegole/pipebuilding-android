package com.bigthinkapps.pipebuilding.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.AndroidViewModel
import com.bigthinkapps.pipebuilding.AppCore

fun AndroidViewModel.getString(resId: Int): String {
    return getApplication<AppCore>().getString(resId)
}

fun AndroidViewModel.getContext(): Context = getApplication<AppCore>()

fun AndroidViewModel.getView(context: Context, layoutIdRes: Int, viewGroup: ViewGroup): View =
    LayoutInflater.from(context).inflate(layoutIdRes, viewGroup, false)