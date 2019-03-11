package com.bigthinkapps.pipebuilding.extension

inline fun <T> T?.ifNotNull(block: (T) -> Unit): T? {
    return this?.also {
        block(it)
    }
}