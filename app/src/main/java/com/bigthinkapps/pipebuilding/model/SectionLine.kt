package com.bigthinkapps.pipebuilding.model

import com.bigthinkapps.pipebuilding.widget.TypePipeline

data class SectionLine(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Int,
    val typePipeline: TypePipeline
)