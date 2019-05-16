package com.bigthinkapps.pipebuilding.model

import com.bigthinkapps.pipebuilding.util.PipeLineSanitaryDiameter

class DataSanitary {
    var pipeLineSanitaryDiameter = PipeLineSanitaryDiameter.getByPosition(0)
    var measurePipeline = 0.0
    var unitsHunter = 0
    var pending = 0
    var dataManifold: DataManifold? = null
    var flow = 0.0

    fun sum(dataUser: DataSanitary) {
        measurePipeline += dataUser.measurePipeline
    }
}