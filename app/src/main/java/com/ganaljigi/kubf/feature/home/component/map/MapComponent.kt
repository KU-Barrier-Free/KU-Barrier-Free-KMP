package com.ganaljigi.kubf.feature.home.component.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ganalijigi.kubf.BuildConfig
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.feature.home.component.map.marker.BuildingMarkerComposable
import com.ganaljigi.kubf.feature.home.component.map.marker.DoorMarkerComposable
import com.ganaljigi.kubf.feature.home.component.map.marker.GateMarkerComposable
import com.ganaljigi.kubf.feature.home.component.map.marker.SpecialMarkerComposable
import com.ganaljigi.kubf.feature.home.component.map.marker.ToggleMarkerComposable
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingMarker
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarker
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.MapMarker
import com.ganaljigi.kubf.feature.home.viewmodel.MarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.SelectableMarker
import com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.ToggleMarker
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HomeMapComponent(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    selectedMarker: SelectableMarker? = null,
    selectedMarkerInfo: MarkerInfo? = null,
    showingMarkers: ImmutableList<MapMarker> = persistentListOf(),
    showingToggleMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    routeResult: RouteResult? = null,
    onBuildingMarkerClick: (BuildingMarker) -> Unit = {},
    onGateMarkerClick: (GateMarker) -> Unit = {},
    onSpecialMarkerClick: (ToggleMarker) -> Unit = {},
    onSpecialImageClick: (List<String>) -> Unit = {},
    onGateImageClick: (List<String>) -> Unit = {},
    onMapClick: () -> Unit = {},
) {
    val markerScale by remember {
        derivedStateOf {
            calculateMarkerScale(
                currentZoom = cameraPositionState.position.zoom,
                minZoom = MapParam.MIN_ZOOM,
                maxZoom = MapParam.MAX_ZOOM,
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        onMapClick = { onMapClick() },
        cameraPositionState = cameraPositionState,
        properties = MapParam.mapProperties.copy(isMyLocationEnabled = false),
        uiSettings = MapParam.mapUiSettings,
        googleMapOptionsFactory = { MapParam.mapOptions },
    ) {
        // 선택된 경로 렌더링
        routeResult?.let { route ->
            if (route.pathPoints.isNotEmpty()) {
                Polyline(
                    points = route.pathPoints,
                    color = MainGreen,
                    width = 10f,
                    zIndex = 2f,
                )
            }
        }

        // 토글 마커 렌더링
        showingToggleMarkers.forEach { marker ->
            when (marker.toggleType) {
                MapToggle.CURB -> {
                    ToggleMarkerComposable(
                        toggleMarker = marker,
                        toggleIconRes = R.drawable.ic_curb_marker,
                        scale = markerScale,
                    )
                }

                MapToggle.SLOPE -> {
                    ToggleMarkerComposable(
                        toggleMarker = marker,
                        toggleIconRes = R.drawable.ic_slope_marker,
                        scale = markerScale,
                    )
                }

                MapToggle.STAIRS -> {
                    ToggleMarkerComposable(
                        toggleMarker = marker,
                        toggleIconRes = R.drawable.ic_stairs_marker,
                        scale = markerScale,
                    )
                }

                MapToggle.SPECIAL_MARK -> {
                    val markerState = rememberMarkerState(
                        position = LatLng(marker.latitude, marker.longitude),
                    )
                    val isSelected =
                        selectedMarker?.id == marker.id && selectedMarker is ToggleMarker
                    val specialInfo = if (isSelected && selectedMarkerInfo is SpecialMarkerInfo) {
                        selectedMarkerInfo
                    } else {
                        null
                    }
                    SpecialMarkerComposable(
                        markerState = markerState,
                        isSelected = isSelected,
                        specialMarkerInfo = specialInfo,
                        onSpecialMarkerClick = { onSpecialMarkerClick(marker) },
                        onSpecialInfoClick = onSpecialImageClick,
                        scale = markerScale,
                    )
                }
            }
        }

        // 마커 렌더링 (건물, 문, 교문)
        showingMarkers.forEach { marker ->
            when (marker) {
                is BuildingMarker -> {
                    val isSelected = selectedMarker?.id == marker.id
                    BuildingMarkerComposable(
                        buildingMarker = marker,
                        isSelected = isSelected,
                        scale = markerScale,
                    ) {
                        onBuildingMarkerClick(it)
                    }
                }

                is DoorMarker -> {
                    DoorMarkerComposable(
                        doorMarker = marker,
                        scale = markerScale,
                    )
                }

                is GateMarker -> {
                    val markerState = rememberMarkerState(
                        position = LatLng(marker.latitude, marker.longitude),
                    )
                    val isSelected = selectedMarker?.id == marker.id
                    val gateInfo = if (isSelected && selectedMarkerInfo is GateMarkerInfo) {
                        selectedMarkerInfo
                    } else {
                        null
                    }
                    GateMarkerComposable(
                        markerState = markerState,
                        isSelected = isSelected,
                        gateMarkerInfo = gateInfo,
                        onGateMarkerClick = { onGateMarkerClick(marker) },
                        onGateInfoClick = onGateImageClick,
                        scale = markerScale,
                    )
                }
            }
        }
    }
}

private fun calculateMarkerScale(
    currentZoom: Float,
    minZoom: Float,
    maxZoom: Float,
): Float {
    val minScale = 0.8f
    val maxScale = 1.0f
    val normalizedZoom = ((currentZoom - minZoom) / (maxZoom - minZoom)).coerceIn(0f, 1f)
    return minScale + (normalizedZoom * (maxScale - minScale))
}

object MapParam {
    const val MIN_ZOOM = 16.0f
    const val MAX_ZOOM = 18.0f

    val mapProperties = MapProperties(
        isBuildingEnabled = true,
        isIndoorEnabled = false,
        isMyLocationEnabled = false,
        isTrafficEnabled = false,
        latLngBoundsForCameraTarget = LatLngBounds(
            LatLng(37.5373, 127.0656),
            LatLng(37.5450, 127.0952),
        ),
        mapStyleOptions = null,
        mapType = MapType.NORMAL,
        maxZoomPreference = MAX_ZOOM,
        minZoomPreference = MIN_ZOOM,
    )
    val mapUiSettings = MapUiSettings(
        compassEnabled = false,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = true,
        scrollGesturesEnabled = true,
        scrollGesturesEnabledDuringRotateOrZoom = true,
        tiltGesturesEnabled = true,
        zoomControlsEnabled = true,
        zoomGesturesEnabled = true,
    )
    val mapOptions = GoogleMapOptions().apply {
        mapId(BuildConfig.GOOGLE_MAPS_ID)
    }
}
