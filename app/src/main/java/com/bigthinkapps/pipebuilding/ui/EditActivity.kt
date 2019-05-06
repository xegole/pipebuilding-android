package com.bigthinkapps.pipebuilding.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bigthinkapps.pipebuilding.BuildConfig
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.*
import com.bigthinkapps.pipebuilding.model.DataGas
import com.bigthinkapps.pipebuilding.model.DataSanitary
import com.bigthinkapps.pipebuilding.model.DataUser
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_CODE_GALLERY
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_PERMISSION_GALLERY
import com.bigthinkapps.pipebuilding.util.DataFinalSectionUtils
import com.bigthinkapps.pipebuilding.util.ExtrasContants
import com.bigthinkapps.pipebuilding.viewmodel.EditViewModel
import com.bigthinkapps.pipebuilding.widget.TypePipeline
import com.itextpdf.text.*
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_edit.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditActivity : AppCompatActivity(), SpeedDialView.OnActionSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(EditViewModel::class.java)
    }

    private val listData = ArrayList<DataUser>()
    private val listDataGas = ArrayList<DataGas>()
    private val listDataSanitary = ArrayList<DataSanitary>()

    private var tempData: DataUser? = null
    private var tempDataGas: DataGas? = null
    private var viscosity: Double = 0.0
    private var currentPressure = 15.0
    private var currentPressureGas = 21.0
    private var typePipeline = TypePipeline.HYDRO
    private var allLosses = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        viewModel.validatePermissions(this)
        initUI()
    }

    private val myColor = WebColors.getRGBColor("#9E9E9E")
    private val myColor1 = WebColors.getRGBColor("#757575")

    private fun createPDF(list: ArrayList<DataSanitary>) {
        val doc = Document()
        try {
            val pathname = Environment.getExternalStorageDirectory().path + "/danahonet/"
            val dir = File(pathname)
            dir.mkdir()
            val file = File(dir, "sanitary.pdf")
            val fOut = FileOutputStream(file)
            PdfWriter.getInstance(doc, fOut)

            //open the document
            doc.open()
            //create table
            val pt = PdfPTable(3)
            pt.widthPercentage = 100f
            val fl = floatArrayOf(20f, 45f, 35f)
            pt.setWidths(fl)
            var cell = PdfPCell()
            cell.border = Rectangle.NO_BORDER

            //set drawable in cell
            val myImage = resources.getDrawable(R.mipmap.ic_launcher)
            val bitmap = (myImage as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bitmapData = stream.toByteArray()
            try {
                val bgImage = Image.getInstance(bitmapData)
                bgImage.setAbsolutePosition(330f, 642f)
                cell.addElement(bgImage)
                pt.addCell(cell)
                cell = PdfPCell()
                cell.border = Rectangle.NO_BORDER
                cell.addElement(Paragraph("Danahonet"))
                pt.addCell(cell)
                cell = PdfPCell(Paragraph(""))
                cell.border = Rectangle.NO_BORDER
                pt.addCell(cell)

                val pTable = PdfPTable(1)
                pTable.widthPercentage = 100f
                cell = PdfPCell()
                cell.colspan = 1
                cell.addElement(pt)
                pTable.addCell(cell)
                val table = PdfPTable(7)

                val columnWidth = floatArrayOf(15f, 10f, 10f, 10f, 10f, 10f, 10f)
                table.setWidths(columnWidth)


                cell = PdfPCell()
                cell.backgroundColor = myColor
                cell.colspan = 7
                cell.addElement(pTable)
                table.addCell(cell)
                cell = PdfPCell(Phrase(" "))
                cell.colspan = 7
                table.addCell(cell)
                cell = PdfPCell()
                cell.colspan = 7

                cell.backgroundColor = myColor1

                cell = PdfPCell(Phrase("Tramo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Qd"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Yd"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Vd"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Dd"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Ad"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Td"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 7

                list.forEach {
                    val dataManifold = it.dataManifold
                    table.addCell("1-2")
                    table.addCell(dataManifold?.qd?.digits(3))
                    table.addCell(dataManifold?.yd?.digits(3))
                    table.addCell(dataManifold?.vd?.digits(3))
                    table.addCell(dataManifold?.dd?.digits(3))
                    table.addCell(dataManifold?.ad?.digits(3))
                    table.addCell(dataManifold?.td?.digits(3))
                }

                table.addCell(cell)
                doc.add(table)
                Toast.makeText(applicationContext, "PDF creado", Toast.LENGTH_LONG).show()
            } catch (de: DocumentException) {
                de.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                doc.close()
            }

            Handler().postDelayed({
                openPdf(file)
            }, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPdf(file: File) {
        val excelPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(excelPath, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No hay apliacion para ver el PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUI() {
        viewModel.addFabActions(speedDialEdit)
        speedDialEdit.setOnActionSelectedListener(this)

        intent.extras.ifNotNull {
            val fileGallery = it.getSerializable(ExtrasContants.EXTRA_GALLERY_FILE) as File
            fileGallery.ifNotNull { file ->
                imageBGCanvas.setImageByFile(file)
            }
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        if (intent.extras == null) {
            showCustomDialog()
        }

        viewModel.inputDataBuilding.observe(this, Observer {
            val measureHeight = it.heightCanvas / height.toDouble()
            val measureWidth = it.widthCanvas / width.toDouble()
            fingerLineEdit.measureHeight = measureHeight
            fingerLineEdit.measureWidth = measureWidth
            viscosity = it.viscosity.toDouble()
        })

        fingerLineEdit.addMeasurePipeline = {
            when (typePipeline) {
                TypePipeline.GAS -> showDialogGas(it)
                TypePipeline.SANITARY -> showDialogSanitary(it)
                else -> showDialogHydro(it)
            }
        }

        fabUndo.setOnClickListener {
            fingerLineEdit.undoSection()
        }

        fabDownPipe.setOnClickListener {
            fingerLineEdit.setDownPipe()
        }
    }

    private fun showDialogSanitary(distance: Double) {
        InputDataSanitaryDialog().show(distance, supportFragmentManager) { dataSanitary, isFinish, lastSection ->
            listDataSanitary.add(dataSanitary)
            if (lastSection) {
                createPDF(listDataSanitary)
            }
            fingerLineEdit.isEditable = true
        }
    }

    private fun showDialogGas(distance: Double) {
        InputDataGasDialog().show(distance, supportFragmentManager) { dataGas, isFinish, lastSection ->
            val data = DataFinalSectionUtils.getFinalVelocity(dataGas, currentPressureGas, allLosses)
            allLosses = data.allLosses

            if (isFinish) {
                fingerLineEdit.initPipeline()

                if (tempDataGas == null) {
                    listDataGas.add(data)
                } else {
                    tempDataGas?.let { item ->
                        item.sum(data)
                        listDataGas.add(item)
                        tempData = null
                    }
                }

                if (lastSection) {
                    ShowDataGasDialog().show(supportFragmentManager, listDataGas)
                }
            } else {
                if (tempDataGas == null) {
                    tempDataGas = data
                } else {
                    tempDataGas?.sum(data)
                }
            }
            fingerLineEdit.isEditable = true
        }
    }

    private fun showDialogHydro(distance: Double) {
        InputUserDataDialog().show(distance, supportFragmentManager) { data, isFinish, lastSection ->
            currentPressure =
                DataFinalSectionUtils.getFinalPressureSection(data, viscosity, currentPressure)
            currentPressure /= 100
            data.pressureFinal = currentPressure

            if (isFinish) {
                fingerLineEdit.initPipeline()
                if (tempData == null) {
                    listData.add(data)
                } else {
                    tempData?.let { dataUser ->
                        dataUser.sum(data)
                        listData.add(dataUser)
                        tempData = null
                    }
                }

                if (lastSection) {
                    ShowDataDialog().show(supportFragmentManager, listData)
                }
            } else {
                if (tempData == null) {
                    tempData = data
                } else {
                    tempData?.sum(data)
                }
            }

            fingerLineEdit.isEditable = true
        }
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem.ifNotNull {
            when (it.id) {
                R.id.fabGallery -> {
                    viewModel.goToGallery(this)
                }
                R.id.fabHydro -> {
                    fabDownPipe.visibility = View.GONE
                    typePipeline = TypePipeline.HYDRO
                    fingerLineEdit.setTypePipeline(TypePipeline.HYDRO)
                }
                R.id.fabGas -> {
                    fabDownPipe.visibility = View.GONE
                    typePipeline = TypePipeline.GAS
                    fingerLineEdit.setTypePipeline(TypePipeline.GAS)
                }
                R.id.fabSanitary -> {
                    typePipeline = TypePipeline.SANITARY
                    fabDownPipe.visibility = View.VISIBLE
                    fingerLineEdit.setTypePipeline(TypePipeline.SANITARY)
                }
                R.id.fabSave -> {
                    containerImageEdit.getBitmapScreen(viewModel::saveProject)
                }
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_GALLERY) {
            viewModel.grantPermissions(this, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
            data.ifNotNull {
                imageBGCanvas.imageByData(it.data!!)
            }
        }
    }

    private fun showCustomDialog() {
        viewModel.showDialogInputData(supportFragmentManager)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar proyecto")
            .setMessage("deseas cerrar el proyecto?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                finish()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
