package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.getInt
import com.bigthinkapps.pipebuilding.model.DataUser
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG_USER
import kotlinx.android.synthetic.main.dialog_input_data_user.*


class InputUserDataDialog : DialogFragment() {

    private val dataUser = DataUser()
    lateinit var result: (DataUser) -> Unit?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_data_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAccept.setOnClickListener { setData() }
    }

    fun show(fragmentManager: FragmentManager, result: (DataUser) -> Unit) {
        show(fragmentManager, TAG_DIALOG_USER)
        this.result = result
    }

    private fun setData() {
        dataUser.hunterUnits = textHunterUnits.getInt()
        dataUser.measurerCapacity = textMeasurerCapacity.getInt()
        result.invoke(dataUser)
        dismiss()
    }
}