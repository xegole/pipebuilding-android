package com.bigthinkapps.pipebuilding.util

enum class PipeLineSanitaryDiameter(
    val value: Double,
    val velocityValue: Double,
    val diameter: Double,
    val position: Int
) {
    PIPE_2(11.03, 5.44, 0.05, 0),
    PIPE_3(32.53, 7.13, 0.08, 1),
    PIPE_4(70.05, 8.64, 0.10, 2),
    PIPE_6(206.54, 11.32, 0.15, 3),
    PIPE_8(444.81, 13.72, 0.20, 4),
    PIPE_10(806.5, 15.92, 0.25, 5),
    PIPE_12(1311.46, 17.97, 0.30, 6);

    companion object {
        fun getByPosition(position: Int): PipeLineSanitaryDiameter {
            return values().first { it.position == position }
        }
    }
}