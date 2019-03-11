package com.bigthinkapps.pipebuilding.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getBitmapScreen
import com.bigthinkapps.pipebuilding.extension.ifNotNull
import com.bigthinkapps.pipebuilding.extension.imageByData
import com.bigthinkapps.pipebuilding.extension.setImageByFile
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_CODE_GALLERY
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_PERMISSION_GALLERY
import com.bigthinkapps.pipebuilding.util.ExtrasContants
import com.bigthinkapps.pipebuilding.viewmodel.EditViewModel
import com.bigthinkapps.pipebuilding.widget.TypePipeline
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_edit.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import java.io.File


class EditActivity : AppCompatActivity(), SpeedDialView.OnActionSelectedListener,
    DiscreteSeekBar.OnProgressChangeListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(EditViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        viewModel.validatePermissions(this)
        initUI()
    }

    private fun initUI() {
        viewModel.addFabActions(speedDialEdit)
        speedDialEdit.setOnActionSelectedListener(this)
        seekBarDiameterPipeline.setOnProgressChangeListener(this)

        intent.extras.ifNotNull {
            val fileGallery = it.getSerializable(ExtrasContants.EXTRA_GALLERY_FILE) as File
            fileGallery.ifNotNull { file ->
                imageBGCanvas.setImageByFile(file)
            }
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
                R.id.fabSave -> {
                    containerImageEdit.getBitmapScreen(viewModel::saveProject)
                }
            }
        }
        return false
    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        fingerLineEdit.setStrokePipeline(value)
    }

    override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
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
}
