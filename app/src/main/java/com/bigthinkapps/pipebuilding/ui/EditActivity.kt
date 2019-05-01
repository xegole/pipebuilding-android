package com.bigthinkapps.pipebuilding.ui

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getBitmapScreen
import com.bigthinkapps.pipebuilding.extension.ifNotNull
import com.bigthinkapps.pipebuilding.extension.imageByData
import com.bigthinkapps.pipebuilding.extension.setImageByFile
import com.bigthinkapps.pipebuilding.model.DataUser
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_CODE_GALLERY
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_PERMISSION_GALLERY
import com.bigthinkapps.pipebuilding.util.DataFinalSectionUtils
import com.bigthinkapps.pipebuilding.util.ExtrasContants
import com.bigthinkapps.pipebuilding.viewmodel.EditViewModel
import com.bigthinkapps.pipebuilding.widget.TypePipeline
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_edit.*
import java.io.File


class EditActivity : AppCompatActivity(), SpeedDialView.OnActionSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(EditViewModel::class.java)
    }

    private val listData = ArrayList<DataUser>()
    private var tempData: DataUser? = null
    private var viscosity: Double = 0.0
    private var currentPressure = 15.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        viewModel.validatePermissions(this)
        initUI()
    }

    private fun initUI() {
        viewModel.addFabActions(speedDialEdit)
        speedDialEdit.setOnActionSelectedListener(this)

        intent.extras.ifNotNull {
            val fileGallery = it.getSerializable(ExtrasContants.EXTRA_GALLERY_FILE) as File
            fileGallery.ifNotNull { file ->
                imageBGCanvas.setImageByFile(file)
            }
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        if (intent.extras == null) {
            showCustomDialog()
        }

        viewModel.inputDataBuilding.observe(this, Observer {
            val measureHeight = it.heightCanvas / height.toDouble()
            val measureWidth = it.widthCanvas / width.toDouble()
            fingerLineEdit.measureHeight = measureHeight
            fingerLineEdit.measureWidth = measureWidth
            viscosity = it.viscosity.toDouble()
        })

        fingerLineEdit.addMeasurePipeline = {
            InputUserDataDialog().show(it, supportFragmentManager) { data, isFinish, lastSection ->
                currentPressure =
                    DataFinalSectionUtils.getFinalPressureSection(data, viscosity, currentPressure)
                currentPressure /= 100
                data.pressureFinal = currentPressure

                if (isFinish) {
                    fingerLineEdit.initPipeline()
                    if (tempData == null) {
                        listData.add(data)
                    } else {
                        tempData?.let { dataUser ->
                            dataUser.sum(data)
                            listData.add(dataUser)
                            tempData = null
                        }
                    }

                    if (lastSection) {
                        ShowDataDialog().show(supportFragmentManager, listData)
                    }
                } else {
                    if (tempData == null) {
                        tempData = data
                    } else {
                        tempData?.sum(data)
                    }
                }

                fingerLineEdit.isEditable = true
            }
        }

        fabUndo.setOnClickListener {
            fingerLineEdit.undoSection()
        }
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem.ifNotNull {
            when (it.id) {
                R.id.fabGallery -> {
                    viewModel.goToGallery(this)
                }
                R.id.fabHydro -> {
                    fingerLineEdit.setTypePipeline(TypePipeline.HYDRO)
                }
                R.id.fabGas -> {
                    fingerLineEdit.setTypePipeline(TypePipeline.GAS)
                }
                R.id.fabSanitary -> {
                    fingerLineEdit.setTypePipeline(TypePipeline.SANITARY)
                }
                R.id.fabSave -> {
                    containerImageEdit.getBitmapScreen(viewModel::saveProject)
                }
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_GALLERY) {
            viewModel.grantPermissions(this, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
            data.ifNotNull {
                imageBGCanvas.imageByData(it.data!!)
            }
        }
    }

    private fun showCustomDialog() {
        viewModel.showDialogInputData(supportFragmentManager)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar proyecto")
            .setMessage("deseas cerrar el proyecto?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                finish()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
