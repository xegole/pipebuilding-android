package com.bigthinkapps.pipebuilding.ui

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getBitmapScreen
import com.bigthinkapps.pipebuilding.extension.ifNotNull
import com.bigthinkapps.pipebuilding.extension.imageByData
import com.bigthinkapps.pipebuilding.extension.setImageByFile
import com.bigthinkapps.pipebuilding.model.DataGas
import com.bigthinkapps.pipebuilding.model.DataSanitary
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
    private val listDataGas = ArrayList<DataGas>()
    private val listDataSanitary = ArrayList<DataSanitary>()

    private var tempData: DataUser? = null
    private var tempDataGas: DataGas? = null
    private var viscosity: Double = 0.0
    private var currentPressure = 15.0
    private var currentPressureGas = 21.0
    private var typePipeline = TypePipeline.HYDRO
    private var allLosses = 0.0

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

        viewModel.openPdfIntent.observe(this, Observer {
            startActivity(it)
        })

        fingerLineEdit.addMeasurePipeline = {
            when (typePipeline) {
                TypePipeline.GAS -> showDialogGas(it)
                TypePipeline.SANITARY -> showDialogSanitary(it)
                TypePipeline.DOWN_PIPE -> showDialogDownPipe()
                else -> showDialogHydro(it)
            }
        }

        fabUndo.setOnClickListener {
            fingerLineEdit.undoSection()
        }

        fabDownPipe.setOnClickListener {
            typePipeline = TypePipeline.DOWN_PIPE
            fingerLineEdit.setDownPipe(true)
        }

        checkHydro.setOnCheckedChangeListener { _, isChecked ->
            fingerLineEdit.editHydro = isChecked
            fingerLineEdit.drawSectionsByFilter()
        }

        checkGas.setOnCheckedChangeListener { _, isChecked ->
            fingerLineEdit.editGas = isChecked
            fingerLineEdit.drawSectionsByFilter()
        }

        checkSanitary.setOnCheckedChangeListener { _, isChecked ->
            fingerLineEdit.editSanitary = isChecked
            fingerLineEdit.drawSectionsByFilter()
        }
    }

    private fun showDialogSanitary(distance: Double) {
        InputDataSanitaryDialog().show(distance, supportFragmentManager) { dataSanitary, isFinish, lastSection ->
            listDataSanitary.add(dataSanitary)
            if (lastSection) {
                viewModel.createPDF(listDataSanitary, resources)
            }

            if (isFinish) {
                fingerLineEdit.initPipeline()
            }

            fingerLineEdit.isEditable = true
        }
    }

    private fun showDialogDownPipe() {
        InputDataDownPipeDialog().show(supportFragmentManager) { dataDownPipe, isFinish, lastSection ->
            dataDownPipe.flow = DataFinalSectionUtils.getFlowDownPipe(dataDownPipe.unitsHunter)
            if (isFinish && lastSection) {
                viewModel.createPdfDownPipe(dataDownPipe, resources)
            }
        }
    }

    private fun showDialogGas(distance: Double) {
        InputDataGasDialog().show(distance, supportFragmentManager) { dataGas, isFinish, lastSection ->
            val data = DataFinalSectionUtils.getFinalVelocity(dataGas, currentPressureGas, allLosses)
            allLosses = data.allLosses

            if (isFinish) {
                fingerLineEdit.initPipeline()

                if (tempDataGas == null) {
                    listDataGas.add(data)
                } else {
                    tempDataGas?.let { item ->
                        item.sum(data)
                        listDataGas.add(item)
                        tempData = null
                    }
                }

                if (lastSection) {
                    viewModel.createPDFGas(listDataGas, resources)
                }
            } else {
                if (tempDataGas == null) {
                    tempDataGas = data
                } else {
                    tempDataGas?.sum(data)
                }
            }
            fingerLineEdit.isEditable = true
        }
    }

    private fun showDialogHydro(distance: Double) {
        InputUserDataDialog().show(distance, supportFragmentManager) { data, isFinish, lastSection ->
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
                    viewModel.createPDFHydro(listData, resources)
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

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem.ifNotNull {
            when (it.id) {
                R.id.fabGallery -> {
                    viewModel.goToGallery(this)
                }
                R.id.fabHydro -> {
                    fingerLineEdit.setDownPipe(false)
                    fingerLineEdit.initPipeline()
                    fabDownPipe.visibility = View.GONE
                    typePipeline = TypePipeline.HYDRO
                    fingerLineEdit.setTypePipeline(TypePipeline.HYDRO)
                }
                R.id.fabGas -> {
                    fingerLineEdit.setDownPipe(false)
                    fingerLineEdit.initPipeline()
                    fabDownPipe.visibility = View.GONE
                    typePipeline = TypePipeline.GAS
                    fingerLineEdit.setTypePipeline(TypePipeline.GAS)
                }
                R.id.fabSanitary -> {
                    fingerLineEdit.setDownPipe(false)
                    fingerLineEdit.initPipeline()
                    typePipeline = TypePipeline.SANITARY
                    fabDownPipe.visibility = View.VISIBLE
                    fingerLineEdit.setTypePipeline(TypePipeline.SANITARY)
                }
                R.id.fabSave -> {
                    containerImageEdit.getBitmapScreen(viewModel::saveProject)
                }
                R.id.fabRci -> InputDataRCIDialog().show(supportFragmentManager) { data, isFinish ->

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
