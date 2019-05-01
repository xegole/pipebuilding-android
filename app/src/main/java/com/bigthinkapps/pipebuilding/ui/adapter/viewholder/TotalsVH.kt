package com.bigthinkapps.pipebuilding.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.AppCore
import com.bigthinkapps.pipebuilding.model.DataUser
import kotlinx.android.synthetic.main.item_totals.view.*

class TotalsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(dataUser: DataUser) {
        itemView.labelSection.text = "1-2"
        itemView.labelDiameter.text = dataUser.diameterPipeline.name
        itemView.labelLongitude.text = AppCore.decimalFormatter.format(dataUser.measurePipeline)
        itemView.labelLosses.text = AppCore.decimalFormatter.format(dataUser.pressureFinal)
    }
}