package com.bigthinkapps.pipebuilding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.model.DataUser
import com.bigthinkapps.pipebuilding.ui.adapter.viewholder.TotalsVH

class TotalsAdapter(private var list: List<DataUser>) : RecyclerView.Adapter<TotalsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalsVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_totals, parent, false)
        return TotalsVH(itemView)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TotalsVH, position: Int) {
        holder.setData(list[position])
    }

    fun setData(list: List<DataUser>) {
        this.list = list
        notifyDataSetChanged()
    }
}