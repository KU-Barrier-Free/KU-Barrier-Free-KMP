package com.ganaljigi.kubf.feature.home.component.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingMarker
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarker
import com.ganaljigi.kubf.feature.home.viewmodel.MapMarker
import com.ganaljigi.kubf.feature.home.viewmodel.MarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.SelectableMarker
import com.ganaljigi.kubf.feature.home.viewmodel.ToggleMarker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 플랫폼별 지도 컴포넌트
 * - Android: Google Maps Compose
 * - iOS: Google Maps via UIKitView
 */
@Composable
expect fun MapComponent(
    modifier: Modifier = Modifier,
    // 카메라 위치
    cameraLatitude: Double,
    cameraLongitude: Double,
    cameraZoom: Float,
    // 마커
    selectedMarker: SelectableMarker? = null,
    selectedMarkerInfo: MarkerInfo? = null,
    showingMarkers: ImmutableList<MapMarker> = persistentListOf(),
    showingToggleMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    routeResult: RouteResult? = null,
    // 이벤트
    onCameraMove: (latitude: Double, longitude: Double, zoom: Float) -> Unit = { _, _, _ -> },
    onBuildingMarkerClick: (BuildingMarker) -> Unit = {},
    onGateMarkerClick: (GateMarker) -> Unit = {},
    onSpecialMarkerClick: (ToggleMarker) -> Unit = {},
    onSpecialImageClick: (List<String>) -> Unit = {},
    onGateImageClick: (List<String>) -> Unit = {},
    onMapClick: () -> Unit = {},
)
