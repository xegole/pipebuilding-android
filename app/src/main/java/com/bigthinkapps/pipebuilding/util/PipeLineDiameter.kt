package com.bigthinkapps.pipebuilding.util

enum class PipeLineDiameter(
    val value1: Double,
    val value2: Double,
    val value3: Double,
    val realDiameter: Double,
    val position: Int
) {
    PIPE_1_2(-0.00000051, 0.00005294, 0.00002122, 0.0166, 0),
    PIPE_3_4(-0.00000030, 0.00004868, 0.00003972, 0.0236, 1),
    PIPE_1(-0.00000024, 0.00004785, 0.00004254, 0.0302, 2);

    companion object {
        fun getDiameterByPosition(position: Int): PipeLineDiameter {
            return values().first { it.position == position }
        }
    }
}