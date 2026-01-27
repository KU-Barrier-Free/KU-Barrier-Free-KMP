package com.ganaljigi.kubf.core.ui.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 위치 권한을 요청합니다.
 * @return 권한이 부여되었으면 true, 거부되었으면 false
 */
suspend fun PermissionsController.requestLocationPermission(): Boolean {
    return try {
        providePermission(Permission.LOCATION)
        true
    } catch (_: DeniedAlwaysException) {
        false
    } catch (_: DeniedException) {
        false
    }
}

/**
 * 위치 권한이 부여되어 있는지 확인합니다.
 */
suspend fun PermissionsController.hasLocationPermission(): Boolean {
    return isPermissionGranted(Permission.LOCATION)
}

/**
 * 위치 권한을 확인하고, 없으면 요청합니다.
 * @return 권한이 부여되었으면 true, 거부되었으면 false
 */
suspend fun PermissionsController.ensureLocationPermission(): Boolean {
    return if (hasLocationPermission()) {
        true
    } else {
        requestLocationPermission()
    }
}

/**
 * 현재 위치를 가져옵니다.
 * @param context Android Context
 * @return 현재 위치의 LatLng, 실패 시 null
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): LatLng? {
    return suspendCoroutine { continuation ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null,
            ).addOnSuccessListener { location ->
                location?.let {
                    continuation.resume(LatLng(it.latitude, it.longitude))
                } ?: continuation.resume(null)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (_: SecurityException) {
            continuation.resume(null)
        }
    }
}

/**
 * 위치 권한을 확인하고 현재 위치를 가져옵니다.
 * @param context Android Context
 * @param permissionsController Moko PermissionsController
 * @return 현재 위치의 LatLng, 권한 거부 또는 실패 시 null
 */
suspend fun getLocationWithPermission(
    context: Context,
    permissionsController: PermissionsController,
): LatLng? {
    return if (permissionsController.ensureLocationPermission()) {
        getCurrentLocation(context)
    } else {
        null
    }
}
