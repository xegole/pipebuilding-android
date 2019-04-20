package com.bigthinkapps.pipebuilding.util

object Utils {

    fun getFlowSection(pipeSizeV1: Double, pipeSizeV2: Double, pipeSizeV3: Double, unitHunter: Int): Double {
        return Math.pow(pipeSizeV1 * unitHunter, 2.0) + (pipeSizeV2 * unitHunter) + pipeSizeV3
    }

    const val PIPE_3_4_V1 = -0.00000051
    const val PIPE_3_4_V2 = 0.00005294
    const val PIPE_3_4_V3 = 0.00002122
}