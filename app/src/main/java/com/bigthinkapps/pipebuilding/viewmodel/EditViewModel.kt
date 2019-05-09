package com.bigthinkapps.pipebuilding.viewmodel

import android.Manifest
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bigthinkapps.pipebuilding.BuildConfig
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.extension.checkPermissions
import com.bigthinkapps.pipebuilding.extension.digits
import com.bigthinkapps.pipebuilding.extension.getString
import com.bigthinkapps.pipebuilding.model.DataBuilding
import com.bigthinkapps.pipebuilding.model.DataDownPipe
import com.bigthinkapps.pipebuilding.model.DataSanitary
import com.bigthinkapps.pipebuilding.ui.InputDataDialog
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_CODE_GALLERY
import com.bigthinkapps.pipebuilding.util.CodeConstants.REQUEST_PERMISSION_GALLERY
import com.bigthinkapps.pipebuilding.util.Constants
import com.itextpdf.text.*
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditViewModel(application: Application) : AndroidViewModel(application) {

    val inputDataBuilding = MutableLiveData<DataBuilding>()
    var nameProject = ""
    val openPdfIntent = MutableLiveData<Intent>()

    fun goToGallery(activity: AppCompatActivity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = Constants.TYPE_GALLERY_IMAGE
        activity.startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    fun validatePermissions(activity: AppCompatActivity) {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!checkPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_GALLERY)
        }
    }

    fun addFabActions(speedDialEdit: SpeedDialView) {
        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabSave,
                R.drawable.ic_edit_save
            ).setLabel(getString(R.string.fab_save)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabGallery,
                R.drawable.ic_add_photo_alternate
            ).setLabel(getString(R.string.fab_gallery)).create()
        )
        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabHydro,
                R.drawable.ic_edit_hidro
            ).setLabel(getString(R.string.fab_hydro)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabGas,
                R.drawable.ic_edit_gas
            ).setLabel(getString(R.string.fab_gas)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabSanitary,
                R.drawable.ic_edit_sanitary
            ).setLabel(getString(R.string.fab_sanitary)).create()
        )

        speedDialEdit.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fabRci,
                R.drawable.ic_rci
            ).setLabel(getString(R.string.fab_rci)).create()
        )
    }

    fun saveProject(saveImageBitmap: Bitmap) {
        try {
            val path = Environment.getExternalStorageDirectory().toString() + Constants.FOLDER_PROJECTS
            File(path).mkdir()
            val name = if (nameProject.isEmpty()) "${System.currentTimeMillis()}" else nameProject
            val file = File(path, "$name.${Constants.IMAGE_EXTENSION}")
            val outputStream = FileOutputStream(file)
            saveImageBitmap.compress(Bitmap.CompressFormat.PNG, Constants.QUALITY_IMAGE, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun grantPermissions(activity: AppCompatActivity, permissions: Array<out String>, grantResults: IntArray) {
        var allGranted = true
        for (i in 0 until permissions.size) {
            val grantResult = grantResults[i]
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break
            }
        }

        if (!allGranted) {
            activity.finish()
        }
    }

    fun showDialogInputData(fragmentManager: FragmentManager) {
        InputDataDialog().show(fragmentManager) {
            inputDataBuilding.value = it
            nameProject = it.nameProject
        }
    }

    private val myColor = WebColors.getRGBColor("#9E9E9E")
    private val myColor1 = WebColors.getRGBColor("#757575")

    fun createPDF(list: ArrayList<DataSanitary>, resources: Resources) {
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

    fun createPdfDownPipe(dataDownPipe: DataDownPipe, resources: Resources) {
        val doc = Document()
        try {
            val pathname = Environment.getExternalStorageDirectory().path + "/danahonet/"
            val dir = File(pathname)
            dir.mkdir()
            val file = File(dir, "downPipe.pdf")
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
                val table = PdfPTable(3)

                val columnWidth = floatArrayOf(10f, 15f, 10f)
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

                cell = PdfPCell(Phrase("Piso"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Unidades hunter"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Caudal"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 3

                table.addCell("1")
                table.addCell(dataDownPipe.unitsHunter.toString())
                table.addCell(dataDownPipe.flow.toString())

                table.addCell(cell)
                doc.add(table)
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
            FileProvider.getUriForFile(getApplication(), BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(excelPath, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        try {
            openPdfIntent.value = pdfIntent
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}