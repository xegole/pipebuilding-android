package com.bigthinkapps.pipebuilding.ui.adapter.viewholder

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.pipebuilding.ui.EditActivity
import com.bigthinkapps.pipebuilding.util.ExtrasContants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_gallery.view.*
import java.io.File

class GalleryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val context by lazy {
        itemView.context
    }

    fun setData(file: File, notify: () -> Unit) {
        Picasso.get().load(file).resize(300, 400).into(itemView.imageProject)
        itemView.labelFileName.text = file.nameWithoutExtension
        itemView.setOnClickListener {
            goToEditScreen(file)
        }

        itemView.setOnLongClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("Eliminar proyecto")
                .setMessage("desea eliminar el proyecto?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    file.delete()
                    notify.invoke()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
            true
        }
    }

    private fun goToEditScreen(file: File) {
        val intent = Intent(context, EditActivity::class.java)
        intent.putExtra(ExtrasContants.EXTRA_GALLERY_FILE, file)
        context.startActivity(intent)
    }
}