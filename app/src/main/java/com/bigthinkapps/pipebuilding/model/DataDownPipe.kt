package com.bigthinkapps.pipebuilding.model

class DataDownPipe {

    var unitsHunter = 0
    var flow = 0.0
    var verticalLongitude = 0.0
    var diameter = 0.0

    fun sum(dataUser: DataDownPipe) {
        unitsHunter += dataUser.unitsHunter
    }
}