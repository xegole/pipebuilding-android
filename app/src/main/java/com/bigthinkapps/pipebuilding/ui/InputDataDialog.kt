package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getInt
import com.bigthinkapps.pipebuilding.model.DataBuilding
import com.bigthinkapps.pipebuilding.util.Constants.FIRST_ITEM
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG
import kotlinx.android.synthetic.main.dialog_input_data_building.*


class InputDataDialog : DialogFragment() {

    private val dataBuilding = DataBuilding()
    lateinit var result: (DataBuilding) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_building, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(view.context, R.array.type_building, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypeBuilding.adapter = adapter
        spinnerTypeBuilding.setSelection(FIRST_ITEM)

        val adapterMeasure = ArrayAdapter.createFromResource(view.context, R.array.measure_type, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMeasureType.adapter = adapterMeasure
        spinnerMeasureType.setSelection(FIRST_ITEM)
        buttonAccept.setOnClickListener { setData() }
    }

    fun show(fragmentManager: FragmentManager, result: (DataBuilding) -> Unit) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = fragmentManager.findFragmentByTag(TAG_DIALOG)
        if (fragment != null) {
            fragmentTransaction.remove(fragment)
        }
        fragmentTransaction.addToBackStack(null)
        show(fragmentTransaction, TAG_DIALOG)
        this.result = result
    }

    private fun setData() {
        dataBuilding.floorsNumber = textFloorsNumber.getInt()
        dataBuilding.heightFloor = textHeightFloor.getInt()
        dataBuilding.temperature = textTemperature.getInt()
        result.invoke(dataBuilding)
        dismiss()
    }
}