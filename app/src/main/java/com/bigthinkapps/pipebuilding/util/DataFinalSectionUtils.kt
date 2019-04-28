package com.bigthinkapps.pipebuilding.util

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
}