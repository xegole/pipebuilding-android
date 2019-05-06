package com.bigthinkapps.pipebuilding.extension

import java.util.*

fun Double.digits(digits: Int): String {
    return String.format(Locale.US, "%.${digits}f", this)
}