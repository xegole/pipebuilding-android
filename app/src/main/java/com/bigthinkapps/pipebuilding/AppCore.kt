package com.bigthinkapps.pipebuilding

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class AppCore : Application() {

    companion object {
        lateinit var decimalFormatter: DecimalFormat

    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        decimalFormatter = DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
        decimalFormatter.roundingMode = RoundingMode.FLOOR
    }
}