package com.bigthinkapps.pipebuilding.extension

fun Double.twoDigits(): String {
    return String.format("%.2f", this)
}