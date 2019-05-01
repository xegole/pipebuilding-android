package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.model.DataUser
import com.bigthinkapps.pipebuilding.ui.adapter.TotalsAdapter
import com.bigthinkapps.pipebuilding.util.Constants.TAG_DIALOG
import kotlinx.android.synthetic.main.dialog_show_data.*


class ShowDataDialog : DialogFragment() {

    private val adapter by lazy {
        TotalsAdapter(emptyList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_show_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        recyclerTotals.adapter = adapter
    }

    fun show(fragmentManager: FragmentManager, list: List<DataUser>) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = fragmentManager.findFragmentByTag(TAG_DIALOG)
        if (fragment != null) {
            fragmentTransaction.remove(fragment)
        }
        fragmentTransaction.addToBackStack(null)
        show(fragmentTransaction, TAG_DIALOG)
        adapter.setData(list)
    }
}