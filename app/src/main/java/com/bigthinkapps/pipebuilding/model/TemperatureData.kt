package com.bigthinkapps.pipebuilding.model

data class TemperatureData(val temperature: Int, val viscosity: String) {

    override fun toString(): String {
        return "$temperature⁰C"
    }
}