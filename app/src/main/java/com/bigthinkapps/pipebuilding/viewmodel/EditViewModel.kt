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
import com.bigthinkapps.pipebuilding.extension.*
import com.bigthinkapps.pipebuilding.model.*
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
    private var nameProject = ""
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

                val numColumns = 9

                val pTable = PdfPTable(1)
                pTable.widthPercentage = 100f
                cell = PdfPCell()
                cell.colspan = 1
                cell.addElement(pt)
                pTable.addCell(cell)
                val table = PdfPTable(numColumns)

                val columnWidth = floatArrayOf(13f, 10f, 15f, 9f, 9f, 9f, 9f, 9f, 9f)
                table.setWidths(columnWidth)


                cell = PdfPCell()
                cell.backgroundColor = myColor
                cell.colspan = numColumns
                cell.addElement(pTable)
                table.addCell(cell)
                cell = PdfPCell(Phrase(" "))
                cell.colspan = numColumns
                table.addCell(cell)
                cell = PdfPCell()
                cell.colspan = numColumns

                cell.backgroundColor = myColor1

                cell = PdfPCell(Phrase("Tramo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Q\nm3/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Unidades acumuladas"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Qd\nm3/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Yd\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Vd\nm/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Dd\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Ad\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Td\nKg/m2"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = numColumns

                list.forEachIndexed { index, dataSanitary ->
                    val dataManifold = dataSanitary.dataManifold
                    table.addCell("${index + 1}-${index + 2}")
                    table.addCell(dataSanitary.flow.digits(3))
                    table.addCell(dataSanitary.unitsHunter.toString())
                    table.addCell(dataManifold?.qd?.digits(4))
                    table.addCell(dataManifold?.yd?.digits(4))
                    table.addCell(dataManifold?.vd?.digits(4))
                    table.addCell(dataManifold?.dd?.digits(4))
                    table.addCell(dataManifold?.ad?.digits(4))
                    table.addCell(dataManifold?.td?.digits(4))
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

    fun createPDFHydro(list: ArrayList<DataUser>, resources: Resources) {
        val doc = Document()
        try {
            val pathname = Environment.getExternalStorageDirectory().path + "/danahonet/"
            val dir = File(pathname)
            dir.mkdir()
            val file = File(dir, "hidraulica.pdf")
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
                val table = PdfPTable(9)
                val columnWidth = floatArrayOf(13f, 18f, 9f, 18f, 15f, 20f, 12f, 12f, 13f)
                table.setWidths(columnWidth)


                cell = PdfPCell()
                cell.backgroundColor = myColor
                cell.colspan = 9
                cell.addElement(pTable)
                table.addCell(cell)
                cell = PdfPCell(Phrase(" "))
                cell.colspan = 9
                table.addCell(cell)
                cell = PdfPCell()
                cell.colspan = 9

                cell.backgroundColor = myColor1

                cell = PdfPCell(Phrase("Tramo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Diámetro"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Q\nm3/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Horizontal\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Vertical\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Accesorios\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Total\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("J\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Presión\nmca"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 9

                list.forEachIndexed { index, dataUser ->
                    table.addCell("${index + 1}-${index + 2}")
                    table.addCell(dataUser.diameterPipeline.realDiameter.toString())
                    table.addCell(dataUser.flowSection.byDigits(6))
                    table.addCell(dataUser.measurePipeline.twoDigits())
                    table.addCell(dataUser.measureVertical.twoDigits())
                    table.addCell(dataUser.measureAccessories.twoDigits())
                    table.addCell(dataUser.measureTotal().twoDigits())
                    table.addCell(dataUser.totalLosses.twoDigits())
                    table.addCell(dataUser.pressureFinal.twoDigits())
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


    fun createPDFGas(list: ArrayList<DataGas>, resources: Resources) {
        val doc = Document()
        try {
            val pathname = Environment.getExternalStorageDirectory().path + "/danahonet/"
            val dir = File(pathname)
            dir.mkdir()
            val file = File(dir, "gas.pdf")
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
                val table = PdfPTable(9)
                val columnWidth = floatArrayOf(9.4f, 10f, 11.5f, 8.0f, 9.4f, 15.5f, 11f, 11f, 13.5f)
                table.setWidths(columnWidth)


                cell = PdfPCell()
                cell.backgroundColor = myColor
                cell.colspan = 9
                cell.addElement(pTable)
                table.addCell(cell)
                cell = PdfPCell(Phrase(" "))
                cell.colspan = 9
                table.addCell(cell)
                cell = PdfPCell()
                cell.colspan = 9

                cell.backgroundColor = myColor1

                cell = PdfPCell(Phrase("Tramo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Caudal\nm3/h"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Diámetro\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Long\nTotal\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Tramo\nPerd\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Acumulada\nm"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Presión\ninicial mbar"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Presión\nfinal mbar"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Velocidad\nm/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 9

                list.forEachIndexed { index, dataGas ->
                    table.addCell("${index + 1}-${index + 2}")
                    table.addCell(dataGas.flow.twoDigits())
                    table.addCell(dataGas.pipeLineGasDiameter.valueName)
                    table.addCell(dataGas.measureTotal.twoDigits())
                    table.addCell(dataGas.sectionLosses.digits(3))
                    table.addCell(dataGas.allLosses.digits(3))
                    table.addCell(dataGas.pressureInitial.twoDigits())
                    table.addCell(dataGas.pressureSection.twoDigits())
                    table.addCell(dataGas.sectionVelocity.twoDigits())
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

    fun createPDFRci(data: DataRci, resources: Resources) {
        val doc = Document()
        try {
            val pathname = Environment.getExternalStorageDirectory().path + "/danahonet/"
            val dir = File(pathname)
            dir.mkdir()
            val file = File(dir, "rci.pdf")
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
                val table = PdfPTable(5)
                val columnWidth = floatArrayOf(13f, 18f, 13f, 18f, 18f)
                table.setWidths(columnWidth)


                cell = PdfPCell()
                cell.backgroundColor = myColor
                cell.colspan = 5
                cell.addElement(pTable)
                table.addCell(cell)
                cell = PdfPCell(Phrase(" "))
                cell.colspan = 5
                table.addCell(cell)
                cell = PdfPCell()
                cell.colspan = 5

                cell.backgroundColor = myColor1

                cell = PdfPCell(Phrase("Grupo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Subgrupo"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Pisos"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Areá a cubrir"))
                cell.backgroundColor = myColor1
                table.addCell(cell)
                cell = PdfPCell(Phrase("Total rociadores"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 5

                table.addCell("Residencial")
                table.addCell(data.subGroup + " " + data.typeGroup)
                table.addCell(data.floors.toString())
                table.addCell(data.coverageArea.toString())
                table.addCell(data.extensorTotals.toString())
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

    fun createPdfDownPipe(list: List<DataDownPipe>, resources: Resources) {
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
                cell = PdfPCell(Phrase("Caudal\nm3/s"))
                cell.backgroundColor = myColor1
                table.addCell(cell)

                cell = PdfPCell()
                cell.colspan = 3

                list.forEachIndexed { index, dataDownPipe ->
                    table.addCell("${index + 1}")
                    table.addCell(dataDownPipe.unitsHunter.toString())
                    table.addCell(dataDownPipe.flow.toString())
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