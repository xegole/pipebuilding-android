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
    PIPE_1(-0.00000024, 0.00004785, 0.00004254, 0.0302, 2),
    PIPE_1_1_4(-0.00000023, 0.00004712, 0.00005389, 0.03814, 3),
    PIPE_1_1_2(-0.00000005, 0.00003023, 0.00019312, 0.0437, 4),
    PIPE_2(-0.00000001, 0.00001792, 0.00094538, 0.0546, 5),
    PIPE_2_1_2(-0.00000001, 0.00001606, 0.00111046, 0.06607, 6),
    PIPE_3(-0.0000000017, 0.00001346, 0.00151492, 0.0804, 7),
    PIPE_4(-0.000000001049, 0.00001128, 0.00257081, 0.10342, 8),
    PIPE_6(-0.0000000002169, 0.00000626, 0.00689609, 0.15222, 9);

    companion object {
        fun getDiameterByPosition(position: Int): PipeLineDiameter {
            return values().first { it.position == position }
        }
    }
}