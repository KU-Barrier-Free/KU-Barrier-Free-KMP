package com.ganaljigi.kubf.core.mapper

import com.ganaljigi.kubf.core.network.response.route.PathResponseDto
import com.ganaljigi.kubf.core.network.response.route.RoutePathDto
import com.ganaljigi.kubf.core.model.RouteMode
import com.ganaljigi.kubf.core.network.response.route.RouteGeometryDto
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double

fun PathResponseDto.toRouteResults(): List<RouteResult> {
    return listOfNotNull(
        shortestPath?.toRouteResult(RouteMode.SHORTEST),
        noStairsPath?.toRouteResult(RouteMode.NO_STAIRS),
        barrierFreePath?.toRouteResult(RouteMode.BARRIER_FREE),
    )
}

fun RoutePathDto.toRouteResult(routeMode: RouteMode): RouteResult {
    val pathPoints = features
        .filter { it.geometry?.type == "LineString" }
        .sortedBy { it.properties?.index ?: 0 }
        .flatMap { it.geometry?.extractLatLngPoints().orEmpty() }
        .removeConsecutiveDuplicates()

    val distanceValue = totalDistance.replace("m", "").toIntOrNull() ?: 0
    val estimatedTime = (distanceValue / 70).coerceAtLeast(1)

    return RouteResult(
        time = estimatedTime,
        distance = distanceValue,
        routeMode = routeMode,
        pathPoints = pathPoints.toImmutableList(),
        distanceText = totalDistance,
    )
}

private fun List<LatLng>.removeConsecutiveDuplicates(): List<LatLng> =
    fold(mutableListOf()) { acc, latLng ->
        if (acc.isEmpty() || acc.last() != latLng) acc.add(latLng)
        acc
    }

private fun RouteGeometryDto.extractLatLngPoints(): List<LatLng> {
    if (type != "LineString" || coordinates !is JsonArray) return emptyList()

    return coordinates.mapNotNull { coordPairElement ->
        if (coordPairElement is JsonArray && coordPairElement.size >= 2) {
            val lng = (coordPairElement[0] as JsonPrimitive).double
            val lat = (coordPairElement[1] as JsonPrimitive).double
            LatLng(lat, lng)
        } else {
            null
        }
    }
}
