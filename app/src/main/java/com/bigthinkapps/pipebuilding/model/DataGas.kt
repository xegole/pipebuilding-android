package com.bigthinkapps.pipebuilding.model

import com.bigthinkapps.pipebuilding.util.PipeLineGasDiameter

class DataGas {
    var pipeLineGasDiameter = PipeLineGasDiameter.getByPosition(0)
    var measurePipeline = 0.0
    var velocityFinal = 0.0
    var flow = 0.0
    var sectionVelocity = 0.0
    var pressureSection = 0.0
    var allLosses = 0.0

    fun sum(dataUser: DataGas) {
        measurePipeline += dataUser.measurePipeline
        velocityFinal += dataUser.velocityFinal
    }
}