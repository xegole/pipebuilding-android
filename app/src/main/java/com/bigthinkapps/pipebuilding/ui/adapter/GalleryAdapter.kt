package com.bigthinkapps.pipebuilding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.R
import java.io.File

class GalleryAdapter(private val listFiles: List<File>) : RecyclerView.Adapter<GalleryVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryVH {
        val containerView = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryVH(containerView)
    }

    override fun getItemCount() = listFiles.size

    override fun onBindViewHolder(holder: GalleryVH, position: Int) {
        holder.setData(listFiles[position])
    }
}