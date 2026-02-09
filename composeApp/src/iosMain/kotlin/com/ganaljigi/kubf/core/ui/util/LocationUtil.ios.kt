package com.ganaljigi.kubf.core.ui.util

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLLocationAccuracyBest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual suspend fun PermissionsController.ensureLocationPermission(): Boolean {
    return try {
        if (isPermissionGranted(Permission.LOCATION)) {
            true
        } else {
            providePermission(Permission.LOCATION)
            true
        }
    } catch (_: DeniedAlwaysException) {
        false
    } catch (_: DeniedException) {
        false
    }
}

actual suspend fun getLocationWithPermission(
    permissionsController: PermissionsController,
): Pair<Double, Double>? {
    return if (permissionsController.ensureLocationPermission()) {
        getCurrentLocationIOS()
    } else {
        null
    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun getCurrentLocationIOS(): Pair<Double, Double>? {
    return suspendCoroutine { continuation ->
        val locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest

        val location = locationManager.location
        if (location != null) {
            location.coordinate.useContents {
                continuation.resume(Pair(latitude, longitude))
            }
        } else {
            continuation.resume(null)
        }
    }
}
