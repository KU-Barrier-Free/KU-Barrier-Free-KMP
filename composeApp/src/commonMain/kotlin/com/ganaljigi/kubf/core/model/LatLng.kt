package com.ganaljigi.kubf.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform LatLng representation
 */
@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

/**
 * Convert to platform-specific LatLng (Google Maps on Android)
 */
expect fun LatLng.toPlatformLatLng(): Any

/**
 * Convert from platform-specific LatLng
 */
expect fun Any.toCommonLatLng(): LatLng
