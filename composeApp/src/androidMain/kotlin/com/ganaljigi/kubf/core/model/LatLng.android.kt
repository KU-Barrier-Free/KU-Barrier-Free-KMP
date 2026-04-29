package com.ganaljigi.kubf.core.model

import com.google.android.gms.maps.model.LatLng as GoogleLatLng

actual fun LatLng.toPlatformLatLng(): Any {
    return GoogleLatLng(latitude, longitude)
}

actual fun Any.toCommonLatLng(): LatLng {
    val googleLatLng = this as GoogleLatLng
    return LatLng(googleLatLng.latitude, googleLatLng.longitude)
}

// Extension for easier conversion
fun LatLng.toGoogleLatLng(): GoogleLatLng = GoogleLatLng(latitude, longitude)
fun GoogleLatLng.toCommon(): LatLng = LatLng(latitude, longitude)
