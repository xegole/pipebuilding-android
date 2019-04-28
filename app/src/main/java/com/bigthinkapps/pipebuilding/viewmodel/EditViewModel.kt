package com.bigthinkapps.pipebuilding.viewmodel

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.checkPermissions
import com.bigthinkapps.pipebuilding.extension.getString
import com.bigthinkapps.pipebuilding.model.DataBuilding
import com.bigthinkapps.pipebuilding.ui.InputDataDialog
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_CODE_GALLERY
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_PERMISSION_GALLERY
import com.bigthinkapps.pipebuilding.util.Constants
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.io.File
import java.io.FileOutputStream


class EditViewModel(application: Application) : AndroidViewModel(application) {

    var buildingType = -1
    var measureType = -1

    val inputDataBuilding = MutableLiveData<DataBuilding>()

    fun goToGallery(activity: AppCompatActivity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = Constants.TYPE_GALLERY_IMAGE
        activity.startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    fun validatePermissions(activity: AppCompatActivity) {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!checkPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_GALLERY)
        }
    }

    fun addFabActions(speedDialEdit: SpeedDialView) {
        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabSave,
                R.drawable.ic_edit_save
            ).setLabel(getString(R.string.fab_save)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabGallery,
                R.drawable.ic_add_photo_alternate
            ).setLabel(getString(R.string.fab_gallery)).create()
        )
        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabHydro,
                R.drawable.ic_edit_hidro
            ).setLabel(getString(R.string.fab_hydro)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabGas,
                R.drawable.ic_edit_gas
            ).setLabel(getString(R.string.fab_gas)).create()
        )
    }

    fun saveProject(saveImageBitmap: Bitmap) {
        try {
            val path = Environment.getExternalStorageDirectory().toString() + Constants.FOLDER_PROJECTS
            File(path).mkdir()
            val file = File(path, "${System.currentTimeMillis()}.${Constants.IMAGE_EXTENSION}")
            val outputStream = FileOutputStream(file)
            saveImageBitmap.compress(Bitmap.CompressFormat.PNG, Constants.QUALITY_IMAGE, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun grantPermissions(activity: AppCompatActivity, permissions: Array<out String>, grantResults: IntArray) {
        var allGranted = true
        for (i in 0 until permissions.size) {
            val grantResult = grantResults[i]
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break
            }
        }

        if (!allGranted) {
            activity.finish()
        }
    }

    fun showDialogInputData(fragmentManager: FragmentManager) {
        InputDataDialog().show(fragmentManager) {
            inputDataBuilding.value = it
        }
    }
}