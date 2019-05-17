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
import com.bigthinkapps.pipebuilding.extension.getDouble
import com.bigthinkapps.pipebuilding.extension.getInt
import com.bigthinkapps.pipebuilding.model.DataRci
import com.bigthinkapps.pipebuilding.util.Constants
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import com.bigthinkapps.pipebuilding.util.DataFinalSectionUtils
import kotlinx.android.synthetic.main.dialog_input_data_rci.*


class InputDataRCIDialog : DialogFragment() {
    private lateinit var result: (DataRci?, Boolean) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_rci, container, false)
    }

    var currentType = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonFinish.setOnClickListener { setData() }

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
                currentType = position
                if (currentType == 0) {
                    inputAreaS.visibility = View.GONE
                    inputAreaC.visibility = View.GONE
                }
            }
        }
    }

    fun show(fragmentManager: FragmentManager, result: (DataRci?, Boolean) -> Unit) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
    }

    private fun setData() {
        val floors = textFloor.getInt()
        if (currentType == 1 && floors >= 7) {
            inputAreaS.visibility = View.VISIBLE
            inputAreaC.visibility = View.VISIBLE

            val areaS = textAreaS.getDouble()
            val areaC = textAreaC.getDouble()

            if (areaC != 0.0 && areaS != 0.0) {
                DataFinalSectionUtils.getRci(areaS, areaC) {
                    it.floors = floors
                    result.invoke(it, true)
                    dismiss()
                }
            }
        } else {
            result.invoke(null, false)
            dismiss()
        }

    }
}