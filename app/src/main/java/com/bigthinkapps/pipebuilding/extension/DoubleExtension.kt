package com.bigthinkapps.pipebuilding.extension

fun Double.twoDigits(): String {
    return String.format("%.2f", this)
}

fun Double.byDigits(digits: Int): String {
    return String.format("%.${digits}f", this)
}