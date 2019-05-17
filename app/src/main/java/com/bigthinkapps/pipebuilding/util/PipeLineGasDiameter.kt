package com.bigthinkapps.pipebuilding.util

enum class PipeLineGasDiameter(
    val value1: Double,
    val position: Int,
    val valueName: String
) {
    PIPE_1_2(15.76, 0, "1/2"),
    PIPE_3_4(20.96, 1, "3/4"),
    PIPE_1(26.64, 2, "1"),
    PIPE_1_1_2(40.94, 3, "1 1/2");

    companion object {
        fun getByPosition(position: Int): PipeLineGasDiameter {
            return values().first { it.position == position }
        }
    }
}