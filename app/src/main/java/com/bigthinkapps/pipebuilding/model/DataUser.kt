package com.bigthinkapps.pipebuilding.model

import com.bigthinkapps.pipebuilding.util.PipeLineDiameter

class DataUser {
    var hunterUnits = 0
    var diameterPipeline = PipeLineDiameter.getDiameterByPosition(0)
    var measurePipeline = 0.0
    var measureAccessories = 0.0
    var measureVertical = 0.0
    var ks = 0.0000015
    var pressureFinal = 0.0
    var flowSection = 0.0
    var totalLosses = 0.0

    fun sum(dataUser: DataUser) {
        hunterUnits += dataUser.hunterUnits
        measurePipeline += dataUser.measurePipeline
        measureVertical += dataUser.measureVertical
        pressureFinal += dataUser.pressureFinal
        measureAccessories += dataUser.measureAccessories
        ks += dataUser.ks
    }

    fun measureTotal() = measurePipeline + measureVertical + measureAccessories
}