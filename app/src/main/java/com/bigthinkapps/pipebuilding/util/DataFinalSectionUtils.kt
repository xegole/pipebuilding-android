package com.bigthinkapps.pipebuilding.util

import com.bigthinkapps.pipebuilding.model.*
import java.math.BigDecimal

object DataFinalSectionUtils {

    private const val PI = 3.141516
    private const val GRAVITY = 9.81

    fun getFinalPressureSection(dataUser: DataUser, viscosity: Double, pressure: Double): Double {
        val realDiameter = dataUser.diameterPipeline.realDiameter
        val measureTotal = dataUser.measureTotal()
        val flowSection = getFlowSection(dataUser.diameterPipeline, dataUser.hunterUnits)
        val speedSection = (flowSection.toDouble() * 4) / (PI * Math.pow(realDiameter, 2.0))
        val lostSpeedSection = Math.pow(speedSection, 2.0) / (2 * GRAVITY)
        val reynoldSection = (realDiameter * speedSection) / viscosity
        val frictionCoefficient = getFrictionCoefficient(dataUser, reynoldSection)
        val unitLosses = frictionCoefficient * (1 / realDiameter) * (Math.pow(speedSection, 2.0) / (2 * GRAVITY))
        val totalLosses = unitLosses * measureTotal

        dataUser.flowSection = flowSection.toDouble()
        dataUser.totalLosses = totalLosses

        return pressure + lostSpeedSection + totalLosses + dataUser.measureVertical
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
                    (ks / (3.7 * realDiameter)) + (2.51 / (reynoldSection * Math.pow(
                        frictionCoefficient,
                        0.5
                    )))
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
            (23200 * longitudeTotal * 0.67 * Math.pow(dataGas.flow, 1.82)) * Math.pow(diameterSI, -4.82)
        val pressureSection = pressure - sectionLosses
        val totalLosses = sectionLosses + allLosses
        val part0 = 0.7236 + ((20.8 - totalLosses) / 1000)
        val part1 = Math.pow(part0, -1.0)
        val sectionVelocity = 354 * dataGas.flow * part1 * Math.pow(diameterSI, -2.0)
        dataGas.sectionVelocity = sectionVelocity
        dataGas.pressureSection = pressureSection
        dataGas.allLosses = totalLosses
        dataGas.pressureInitial = pressure
        dataGas.measureTotal = longitudeTotal
        dataGas.sectionLosses = sectionLosses
        return dataGas
    }

    fun getFlowQo(dataSanitary: DataSanitary, listener: (Double) -> Unit): Double {
        val valueDataPipeline = dataSanitary.pipeLineSanitaryDiameter
        val flow = 0.0004 * Math.pow(dataSanitary.unitsHunter.toDouble(), 0.5196)
        val flowQo = (valueDataPipeline.value * Math.sqrt(dataSanitary.pending / 100.0)) / 1000
        listener.invoke(flow)
        return flow / flowQo
    }

    fun getDataSanitary(dataSanitary: DataSanitary, yφo: Double, vVo: Double, dφo: Double, aAo: Double, tTo: Double):
            DataManifold {
        val valueDataPipeline = dataSanitary.pipeLineSanitaryDiameter
        val flow = 0.0004 * Math.pow(dataSanitary.unitsHunter.toDouble(), 0.5196)
        val flowQo = (valueDataPipeline.value * Math.sqrt(dataSanitary.pending / 100.0)) / 1000
        val velocityVo = valueDataPipeline.velocityValue * Math.sqrt(dataSanitary.pending / 100.0)
        val forceT = 250 * valueDataPipeline.diameter * (dataSanitary.pending / 100.0)
        val qd = (flow / flowQo) * flowQo
        val yd = yφo * valueDataPipeline.diameter
        val vd = vVo * velocityVo
        val dd = dφo * valueDataPipeline.diameter
        val ad = aAo * ((PI * Math.pow(valueDataPipeline.diameter, 2.0)) / 4)
        val td = tTo * forceT
        return DataManifold(qd, yd, vd, dd, ad, td)
    }

    fun getFlowDownPipe(unitHunter: Int): Double {
        return 0.0004 * Math.pow(unitHunter.toDouble(), 0.5196)
    }

    fun getRci(areaS: Double, areaC: Double, listener: (DataRci) -> Unit) {
        val totalCoverage = areaS + areaC
        val extensorTotals = totalCoverage / 19.63
        val dataRci = DataRci()
        dataRci.coverageArea = totalCoverage
        dataRci.extensorTotals = extensorTotals.toInt()
        listener.invoke(dataRci)

    }
}