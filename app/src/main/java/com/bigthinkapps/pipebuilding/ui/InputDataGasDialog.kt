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
import com.bigthinkapps.pipebuilding.extension.getDouble
import com.bigthinkapps.pipebuilding.model.DataGas
import com.bigthinkapps.pipebuilding.util.Constants
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import com.bigthinkapps.pipebuilding.util.PipeLineGasDiameter
import kotlinx.android.synthetic.main.dialog_input_data_gas.*


class InputDataGasDialog : DialogFragment() {

    private val dataGas = DataGas()
    private lateinit var result: (DataGas, Boolean, Boolean) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_gas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAccept.setOnClickListener { setData(false) }
        buttonFinish.setOnClickListener { setData(true) }

        val labelMeasurePipelineText = "Longitud aprox ${AppCore.decimalFormatter.format(dataGas.measurePipeline)} m"
        labelMeasurePipeline.text = labelMeasurePipelineText

        val adapter =
            ArrayAdapter.createFromResource(view.context, R.array.type_diameter_pipeline_gas, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiameterPipeline.adapter = adapter
        spinnerDiameterPipeline.setSelection(Constants.FIRST_ITEM)
        spinnerDiameterPipeline.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dataGas.pipeLineGasDiameter = PipeLineGasDiameter.getByPosition(position)
            }
        }
    }

    fun show(measurePipeline: Double, fragmentManager: FragmentManager, result: (DataGas, Boolean, Boolean) -> Unit) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
        dataGas.measurePipeline = measurePipeline
    }

    private fun setData(isFinish: Boolean) {
        dataGas.flow = textFlow.getDouble()
        result.invoke(dataGas, isFinish, checkLastRoute.isChecked)
        dismiss()
    }
}