package com.ganaljigi.kubf.core.model

import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake

actual fun LatLng.toPlatformLatLng(): Any {
    return CLLocationCoordinate2DMake(latitude, longitude)
}

actual fun Any.toCommonLatLng(): LatLng {
    val coordinate = this as CLLocationCoordinate2D
    return LatLng(coordinate.latitude, coordinate.longitude)
}

// Extension for easier conversion
fun LatLng.toCLLocationCoordinate2D(): CLLocationCoordinate2D = CLLocationCoordinate2DMake(latitude, longitude)
