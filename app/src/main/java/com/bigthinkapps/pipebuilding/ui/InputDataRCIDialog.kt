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
import com.bigthinkapps.pipebuilding.model.DataSanitary
import com.bigthinkapps.pipebuilding.util.Constants
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import com.bigthinkapps.pipebuilding.util.PipeLineSanitaryDiameter
import kotlinx.android.synthetic.main.dialog_input_data_rci.*


class InputDataRCIDialog : DialogFragment() {

    private val dataSanitary = DataSanitary()
    private lateinit var result: (DataSanitary, Boolean) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_rci, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonFinish.setOnClickListener { setData(true) }

        val adapter =
            ArrayAdapter.createFromResource(
                view.context,
                R.array.type_sub_group,
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

    fun show(fragmentManager: FragmentManager, result: (DataSanitary, Boolean) -> Unit) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
    }

    private fun setData(isFinish: Boolean) {
        result.invoke(dataSanitary, isFinish)
        dismiss()
    }
}