package com.bigthinkapps.pipebuilding.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bigthinkapps.pipebuilding.util.Constants
import java.io.File


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val listFiles = MutableLiveData<List<File>>()

    fun loadGallery() {
        val path = Environment.getExternalStorageDirectory().toString() + Constants.FOLDER_PROJECTS
        File(path).mkdir()
        val directory = File(path)
        val files = directory.listFiles()
        if (files.isNullOrEmpty()) {
            listFiles.value = null
        } else {
            listFiles.value = files.filter { it.extension == Constants.IMAGE_EXTENSION }
        }
    }
}