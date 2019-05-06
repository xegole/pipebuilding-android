package com.bigthinkapps.pipebuilding.util

import com.bigthinkapps.pipebuilding.model.DataGas
import com.bigthinkapps.pipebuilding.model.DataSanitary
import com.bigthinkapps.pipebuilding.model.DataUser
import java.math.BigDecimal

object DataFinalSectionUtils {

    private const val PI = 3.141516
    private const val GRAVITY = 9.81

    fun getFinalPressureSection(dataUser: DataUser, viscosity: Double, pressure: Double): Double {
        val realDiameter = dataUser.diameterPipeline.realDiameter
        val flowSection = getFlowSection(dataUser.diameterPipeline, dataUser.hunterUnits)
        val speedSection = (flowSection.toDouble() * 4) / (PI * Math.pow(realDiameter, 2.0))
        val lostSpeedSection = Math.pow(speedSection, 2.0) / (2 * GRAVITY)
        val reynoldSection = (dataUser.diameterPipeline.realDiameter * speedSection) / viscosity
        val frictionCoefficient = getFrictionCoefficient(dataUser, reynoldSection)
        val unitLosses = frictionCoefficient * (1 / realDiameter) * (speedSection / 2 * GRAVITY)
        val totalLosses = unitLosses * dataUser.measurePipeline
        return pressure + lostSpeedSection + totalLosses
    }

    private fun getFlowSection(diameter: PipeLineDiameter, totalUnitsHunter: Int): BigDecimal {
        return Utils.getFlowSection(diameter.value1, diameter.value2, diameter.value3, totalUnitsHunter)
    }

    private fun getFrictionCoefficient(dataUser: DataUser, reynoldSection: Double): Double {
        val realDiameter = dataUser.diameterPipeline.realDiameter
        val ks = dataUser.ks

        var frictionCoefficient = 0.001

        for (i in 0..100) {
            val fi = 1 / Math.pow(
                -2 * (Math.log(
                    (ks / (3.7 * realDiameter)) + (2.51 / Math.pow(
                        reynoldSection * frictionCoefficient,
                        0.5
                    ))
                ) / Math.log(10.0)), 2.0
            )
            frictionCoefficient = fi
        }
        return frictionCoefficient
    }

    fun getFinalVelocity(dataGas: DataGas, pressure: Double, allLosses: Double): DataGas {
        val diameterSI = dataGas.pipeLineGasDiameter.value1
        val longitudeProm = dataGas.measurePipeline * 0.2
        val longitudeTotal = longitudeProm + dataGas.measurePipeline
        val sectionLosses =
            (23200 * longitudeTotal * longitudeProm * Math.pow(dataGas.flow, 1.82)) * Math.pow(diameterSI, -4.82)
        val pressureSection = pressure - sectionLosses
        val totalLosses = sectionLosses + allLosses
        val sectionVelocity =
            254 * dataGas.flow * (0.7236 + Math.pow((20.8 - totalLosses) / 1000, -1.0)) * Math.pow(diameterSI, -2.0)
        dataGas.sectionVelocity = sectionVelocity
        dataGas.pressureSection = pressureSection
        dataGas.allLosses = totalLosses
        return dataGas
    }

    fun getFlowQo(dataSanitary: DataSanitary): Double {
        val valueDataPipeline = dataSanitary.pipeLineSanitaryDiameter
        val flow = 0.0004 * Math.pow(dataSanitary.unitsHunter.toDouble(), 0.5196)
        val flowQo = (valueDataPipeline.value * Math.sqrt(dataSanitary.pending / 100.0)) / 1000
        return flow / flowQo
    }
}