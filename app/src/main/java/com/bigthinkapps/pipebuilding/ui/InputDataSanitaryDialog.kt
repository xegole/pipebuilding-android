package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bigthinkapps.pipebuilding.AppCore
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.digits
import com.bigthinkapps.pipebuilding.extension.getDouble
import com.bigthinkapps.pipebuilding.extension.getInt
import com.bigthinkapps.pipebuilding.model.DataSanitary
import com.bigthinkapps.pipebuilding.util.Constants
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import com.bigthinkapps.pipebuilding.util.DataFinalSectionUtils
import com.bigthinkapps.pipebuilding.util.PipeLineSanitaryDiameter
import kotlinx.android.synthetic.main.dialog_input_data_sanitary.*


class InputDataSanitaryDialog : DialogFragment() {

    private val dataSanitary = DataSanitary()
    private lateinit var result: (DataSanitary, Boolean, Boolean) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_sanitary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAccept.setOnClickListener { setData(false) }
        buttonFinish.setOnClickListener { setData(true) }
        buttonFlowPipe.setOnClickListener { flowPipe() }

        val labelMeasurePipelineText =
            "Longitud aprox ${AppCore.decimalFormatter.format(dataSanitary.measurePipeline)} m"
        labelMeasurePipeline.text = labelMeasurePipelineText

        val adapter =
            ArrayAdapter.createFromResource(
                view.context,
                R.array.type_diameter_pipeline_sanitary,
                R.layout.spinner_item
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiameterPipeline.adapter = adapter
        spinnerDiameterPipeline.setSelection(Constants.FIRST_ITEM)
        spinnerDiameterPipeline.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dataSanitary.pipeLineSanitaryDiameter = PipeLineSanitaryDiameter.getByPosition(position)
            }
        }
    }

    fun show(
        measurePipeline: Double,
        fragmentManager: FragmentManager,
        result: (DataSanitary, Boolean, Boolean) -> Unit
    ) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
        dataSanitary.measurePipeline = measurePipeline
    }

    private fun flowPipe() {
        dataSanitary.unitsHunter = textHunterUnits.getInt()
        dataSanitary.pending = textKs.getInt()
        labelBaseFlow.text = DataFinalSectionUtils.getFlowQo(dataSanitary).digits(2)
    }

    private fun setData(isFinish: Boolean) {
        val dataManifold = DataFinalSectionUtils.getDataSanitary(
            dataSanitary,
            textYφo.getDouble(),
            textVVo.getDouble(),
            textDφo.getDouble(),
            textAAo.getDouble(),
            textTTo.getDouble()
        )
        dataSanitary.dataManifold = dataManifold
        result.invoke(dataSanitary, isFinish, checkLastRoute.isChecked)
        dismiss()
    }
}