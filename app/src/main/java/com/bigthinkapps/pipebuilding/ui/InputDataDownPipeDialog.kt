package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getInt
import com.bigthinkapps.pipebuilding.model.DataDownPipe
import com.bigthinkapps.pipebuilding.util.Constants
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import com.bigthinkapps.pipebuilding.util.DataFinalSectionUtils
import com.bigthinkapps.pipebuilding.util.PipeLineSanitaryDiameter
import kotlinx.android.synthetic.main.dialog_input_data_downpipe.*


class InputDataDownPipeDialog : DialogFragment() {

    private val dataDownPipe = DataDownPipe()
    private val dataSanitaryTemp = DataDownPipe()
    private lateinit var result: (DataDownPipe, Boolean, Boolean) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.dialog_input_data_downpipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonFinish.setOnClickListener { setData() }

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
                dataSanitaryTemp.diameter = PipeLineSanitaryDiameter.getByPosition(position).diameter
            }
        }
    }

    fun show(fragmentManager: FragmentManager, result: (DataDownPipe, Boolean, Boolean) -> Unit) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
    }

    private fun setData() {
        dataDownPipe.unitsHunter = textHunterUnits.getInt()
        dataDownPipe.flow = DataFinalSectionUtils.getFlowDownPipe(textHunterUnits.getInt())
        result.invoke(dataDownPipe, checkPlusFloor.isChecked, checkLastRoute.isChecked)
        dismiss()
    }
}