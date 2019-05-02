package com.bigthinkapps.pipebuilding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.model.DataGas
import com.bigthinkapps.pipebuilding.ui.adapter.viewholder.GasDataVH

class GasDataAdapter(private var list: List<DataGas>) : RecyclerView.Adapter<GasDataVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GasDataVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_totals, parent, false)
        return GasDataVH(itemView)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: GasDataVH, position: Int) {
        holder.setData(list[position])
    }

    fun setData(list: List<DataGas>) {
        this.list = list
        notifyDataSetChanged()
    }
}