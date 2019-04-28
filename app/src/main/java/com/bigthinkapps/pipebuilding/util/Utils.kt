package com.bigthinkapps.pipebuilding.util

import android.content.res.Resources
import com.bigthinkapps.pipebuilding.R
import com.bigthinkapps.pipebuilding.model.TemperatureData
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.math.BigDecimal


object Utils {

    fun getFlowSection(pipeSizeV1: Double, pipeSizeV2: Double, pipeSizeV3: Double, unitHunter: Int): BigDecimal {
        return (Math.pow(pipeSizeV1 * unitHunter, 2.0) + (pipeSizeV2 * unitHunter) + pipeSizeV3).toBigDecimal()
    }

    fun getViscosity(resources: Resources, temperature: Int): Double {
        val inputStream = resources.openRawResource(R.raw.viscosity)
        var temperatureData = TemperatureData(20, "0,000001009")
        inputStream.use { input ->
            val bufferedReader = BufferedReader(InputStreamReader(input, "UTF-8") as Reader?)
            val stringReader = bufferedReader.use { it.readText() }
            val listTemperatures = Gson().fromJson(stringReader, Array<TemperatureData>::class.java).toList()
            if (listTemperatures.isNotEmpty()) {
                temperatureData = listTemperatures.first { it.temperature == temperature }
            }
        }
        return temperatureData.viscosity.toDouble()
    }

    fun getTemperatureList(resources: Resources): Array<TemperatureData> {
        val inputStream = resources.openRawResource(R.raw.viscosity)
        var list = emptyArray<TemperatureData>()
        inputStream.use { input ->
            val bufferedReader = BufferedReader(InputStreamReader(input, "UTF-8") as Reader?)
            val stringReader = bufferedReader.use { it.readText() }
            list = Gson().fromJson(stringReader, Array<TemperatureData>::class.java)
        }
        return list
    }
}