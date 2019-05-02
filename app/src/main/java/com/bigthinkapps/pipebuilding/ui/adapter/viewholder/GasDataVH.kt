package com.bigthinkapps.pipebuilding.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.AppCore
import com.bigthinkapps.pipebuilding.model.DataGas
import kotlinx.android.synthetic.main.item_totals.view.*

class GasDataVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(dataGas: DataGas) {
        itemView.labelSection.text = "1-2"
        itemView.labelDiameter.text = dataGas.pipeLineGasDiameter.name
        itemView.labelLongitude.text = AppCore.decimalFormatter.format(dataGas.measurePipeline)
        itemView.labelLosses.text = AppCore.decimalFormatter.format(dataGas.allLosses)
    }
}