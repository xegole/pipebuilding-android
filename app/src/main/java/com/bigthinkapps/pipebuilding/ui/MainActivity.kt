package com.bigthinkapps.pipebuilding.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.helpers.goToActivity
import com.bigthinkapps.pipebuilding.ui.adapter.GalleryAdapter
import com.bigthinkapps.pipebuilding.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabEdit.setOnClickListener { goToActivity(EditActivity::class.java) }
        initUI()
    }

    private fun initUI() {
        toolbarGallery.title = getString(R.string.label_gallery)
        setSupportActionBar(toolbarGallery)
        viewModel.listFiles.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                val adapter = GalleryAdapter(it)
                recyclerViewFiles.adapter = adapter
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadGallery()
    }
}
