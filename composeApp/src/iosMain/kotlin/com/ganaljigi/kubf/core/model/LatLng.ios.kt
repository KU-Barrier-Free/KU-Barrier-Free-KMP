package com.ganaljigi.kubf.core.model

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake

@OptIn(ExperimentalForeignApi::class)
actual fun LatLng.toPlatformLatLng(): Any {
    return CLLocationCoordinate2DMake(latitude, longitude)
}

@OptIn(ExperimentalForeignApi::class)
actual fun Any.toCommonLatLng(): LatLng {
    @Suppress("UNCHECKED_CAST")
    val coordinate = this as kotlinx.cinterop.CValue<CLLocationCoordinate2D>
    return coordinate.useContents {
        LatLng(latitude, longitude)
    }
}

// Extension for easier conversion
@OptIn(ExperimentalForeignApi::class)
fun LatLng.toCLLocationCoordinate2D() = CLLocationCoordinate2DMake(latitude, longitude)
