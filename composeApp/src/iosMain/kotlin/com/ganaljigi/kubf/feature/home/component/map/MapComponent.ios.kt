package com.ganaljigi.kubf.feature.home.component.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.GoogleMaps.CLLocationCoordinate2D
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSMapID
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingMarker
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarker
import com.ganaljigi.kubf.feature.home.viewmodel.MapMarker
import com.ganaljigi.kubf.feature.home.viewmodel.MarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.SelectableMarker
import com.ganaljigi.kubf.feature.home.viewmodel.ToggleMarker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import kotlinx.collections.immutable.ImmutableList
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSBundle
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.darwin.NSObject
import kotlin.math.absoluteValue

// Android MapParam과 동일한 설정
private object MapParam {
    const val MIN_ZOOM = 16.0f
    const val MAX_ZOOM = 18.0f

    // 건국대학교 캠퍼스 영역 (Android와 동일)
    const val SOUTH_WEST_LAT = 37.5373
    const val SOUTH_WEST_LNG = 127.0656
    const val NORTH_EAST_LAT = 37.5450
    const val NORTH_EAST_LNG = 127.0952

    // 마커 스케일 계산 (Android와 동일)
    fun calculateMarkerScale(currentZoom: Float): Float {
        val minScale = 0.8f
        val maxScale = 1.0f
        val normalizedZoom = ((currentZoom - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM)).coerceIn(0f, 1f)
        return minScale + (normalizedZoom * (maxScale - minScale))
    }

    // MainGreen 색상 (0xFF00C73C)
    val mainGreenColor = UIColor.colorWithRed(0.0, green = 199.0/255.0, blue = 60.0/255.0, alpha = 1.0)
}

/**
 * UIImage 크기 조절 (Android dp와 동일하게)
 */
@OptIn(ExperimentalForeignApi::class)
private fun UIImage.scaled(width: Double, height: Double): UIImage? {
    return kotlinx.cinterop.memScoped {
        val size = CGSizeMake(width, height)
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        drawInRect(CGRectMake(0.0, 0.0, width, height))
        val scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        scaledImage
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComponent(
    modifier: Modifier,
    cameraLatitude: Double,
    cameraLongitude: Double,
    cameraZoom: Float,
    selectedMarker: SelectableMarker?,
    selectedMarkerInfo: MarkerInfo?,
    showingMarkers: ImmutableList<MapMarker>,
    showingToggleMarkers: ImmutableList<ToggleMarker>,
    routeResult: RouteResult?,
    onCameraMove: (latitude: Double, longitude: Double, zoom: Float) -> Unit,
    onBuildingMarkerClick: (BuildingMarker) -> Unit,
    onGateMarkerClick: (GateMarker) -> Unit,
    onSpecialMarkerClick: (ToggleMarker) -> Unit,
    onSpecialImageClick: (List<String>) -> Unit,
    onGateImageClick: (List<String>) -> Unit,
    onMapClick: () -> Unit,
    isMyLocationEnabled: Boolean,
) {
    // delegate가 최신 상태/콜백에 접근할 수 있도록 State로 관리
    val markerInfoState = remember { mutableStateOf<MarkerInfo?>(null) }

    // 콜백들을 rememberUpdatedState로 관리하여 delegate가 항상 최신 콜백 사용
    val currentOnCameraMove = rememberUpdatedState(onCameraMove)
    val currentOnBuildingMarkerClick = rememberUpdatedState(onBuildingMarkerClick)
    val currentOnGateMarkerClick = rememberUpdatedState(onGateMarkerClick)
    val currentOnSpecialMarkerClick = rememberUpdatedState(onSpecialMarkerClick)
    val currentOnSpecialImageClick = rememberUpdatedState(onSpecialImageClick)
    val currentOnGateImageClick = rememberUpdatedState(onGateImageClick)
    val currentOnMapClick = rememberUpdatedState(onMapClick)

    // 마지막으로 맵에 설정한 카메라 위치 추적 (불필요한 카메라 이동 방지)
    val lastSetCameraLat = remember { mutableStateOf(cameraLatitude) }
    val lastSetCameraLng = remember { mutableStateOf(cameraLongitude) }
    val lastSetCameraZoom = remember { mutableStateOf(cameraZoom) }

    // 마커 rebuild 판단용 이전 상태 추적
    val prevShowingMarkers = remember { mutableStateOf(showingMarkers) }
    val prevShowingToggleMarkers = remember { mutableStateOf(showingToggleMarkers) }
    val prevRouteResult = remember { mutableStateOf(routeResult) }
    val prevSelectedMarker = remember { mutableStateOf(selectedMarker) }
    val prevSelectedMarkerInfo = remember { mutableStateOf<MarkerInfo?>(null) }
    val prevCameraZoom = remember { mutableStateOf(cameraZoom) }

    // GMSMarker 참조 저장 (clear 없이 개별 마커 업데이트 가능하게)
    val gmsMarkerMap = remember { mutableStateOf(mutableMapOf<String, GMSMarker>()) }

    // Delegate를 remember로 유지하여 가비지 컬렉션 방지
    val delegate = remember {
        object : NSObject(), GMSMapViewDelegateProtocol {
            // 카메라 이동 완료 시 호출
            @ObjCSignatureOverride
            override fun mapView(mapView: GMSMapView, idleAtCameraPosition: GMSCameraPosition) {
                idleAtCameraPosition.target.useContents {
                    currentOnCameraMove.value(latitude, longitude, idleAtCameraPosition.zoom)
                }
            }

            @ObjCSignatureOverride
            override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
                val markerData = didTapMarker.userData
                when (markerData) {
                    is BuildingMarker -> {
                        currentOnBuildingMarkerClick.value(markerData)
                        return true
                    }
                    is GateMarker -> {
                        currentOnGateMarkerClick.value(markerData)
                        return true
                    }
                    is ToggleMarker -> {
                        if (markerData.toggleType == MapToggle.SPECIAL_MARK) {
                            currentOnSpecialMarkerClick.value(markerData)
                        }
                        return true
                    }
                }
                return true
            }

            override fun mapView(mapView: GMSMapView, didTapAtCoordinate: kotlinx.cinterop.CValue<CLLocationCoordinate2D>) {
                currentOnMapClick.value()
            }

            @ObjCSignatureOverride
            override fun mapView(mapView: GMSMapView, markerInfoWindow: GMSMarker): platform.UIKit.UIView? {
                val markerData = markerInfoWindow.userData
                val currentInfo = markerInfoState.value
                return when (markerData) {
                    is GateMarker -> {
                        val gateInfo = currentInfo as? com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo
                        if (gateInfo != null) {
                            if (InfoWindowImageCache.needsDownload(gateInfo.imageUrls)) {
                                InfoWindowImageCache.downloadImages(gateInfo.imageUrls) {
                                    val selected = mapView.selectedMarker
                                    if (selected != null) {
                                        mapView.selectedMarker = null
                                        mapView.selectedMarker = selected
                                    }
                                }
                            }
                            createGateInfoWindowView(markerData.name, gateInfo.description, gateInfo.imageUrls)
                        } else {
                            null
                        }
                    }
                    is ToggleMarker -> {
                        if (markerData.toggleType == MapToggle.SPECIAL_MARK) {
                            val specialInfo = currentInfo as? com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
                            if (specialInfo != null) {
                                if (InfoWindowImageCache.needsDownload(specialInfo.imageUrls)) {
                                    InfoWindowImageCache.downloadImages(specialInfo.imageUrls) {
                                        val selected = mapView.selectedMarker
                                        if (selected != null) {
                                            mapView.selectedMarker = null
                                            mapView.selectedMarker = selected
                                        }
                                    }
                                }
                                createSpecialInfoWindowView(specialInfo.description, specialInfo.imageUrls)
                            } else {
                                null
                            }
                        } else null
                    }
                    else -> null
                }
            }

            @ObjCSignatureOverride
            override fun mapView(mapView: GMSMapView, didTapInfoWindowOfMarker: GMSMarker) {
                val markerData = didTapInfoWindowOfMarker.userData
                val currentInfo = markerInfoState.value
                when (markerData) {
                    is GateMarker -> {
                        val gateInfo = currentInfo as? com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo
                        val imageUrls = gateInfo?.imageUrls ?: emptyList()
                        if (imageUrls.isNotEmpty()) {
                            currentOnGateImageClick.value(imageUrls)
                        }
                    }
                    is ToggleMarker -> {
                        if (markerData.toggleType == MapToggle.SPECIAL_MARK) {
                            val specialInfo = currentInfo as? com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
                            val imageUrls = specialInfo?.imageUrls ?: emptyList()
                            if (imageUrls.isNotEmpty()) {
                                currentOnSpecialImageClick.value(imageUrls)
                            }
                        }
                    }
                }
            }
        }
    }

    UIKitView(
        factory = {
            val camera = GMSCameraPosition.cameraWithLatitude(
                latitude = cameraLatitude,
                longitude = cameraLongitude,
                zoom = cameraZoom
            )
            val frame = CGRectMake(0.0, 0.0, 0.0, 0.0)

            // Map ID 적용 (Android와 동일)
            val mapID = cocoapods.GoogleMaps.GMSMapID(identifier = "fd79985b236a7f2549a74b62")
            val mapView = GMSMapView.mapWithFrame(
                frame = frame,
                mapID = mapID,
                camera = camera
            )

            // Delegate 설정
            mapView.delegate = delegate

            // 지도 설정 (Android와 동일하게)
            mapView.setMyLocationEnabled(isMyLocationEnabled)
            mapView.setMinZoom(MapParam.MIN_ZOOM, maxZoom = MapParam.MAX_ZOOM)

            // 카메라 이동 영역 제한 (건국대 캠퍼스)
            val southWest = cValue<CLLocationCoordinate2D> {
                latitude = MapParam.SOUTH_WEST_LAT
                longitude = MapParam.SOUTH_WEST_LNG
            }
            val northEast = cValue<CLLocationCoordinate2D> {
                latitude = MapParam.NORTH_EAST_LAT
                longitude = MapParam.NORTH_EAST_LNG
            }
            val bounds = GMSCoordinateBounds(southWest, northEast)
            mapView.setCameraTargetBounds(bounds)

            // UI 설정 (Android와 동일)
            mapView.settings?.setMyLocationButton(false)
            mapView.settings?.setCompassButton(false)
            mapView.settings?.setZoomGestures(true)
            mapView.settings?.setScrollGestures(true)
            mapView.settings?.setRotateGestures(true)
            mapView.settings?.setTiltGestures(true)
            mapView.settings?.setAllGesturesEnabled(true)

            mapView
        },
        modifier = modifier,
        update = { mapView ->
            markerInfoState.value = selectedMarkerInfo
            mapView.setMyLocationEnabled(isMyLocationEnabled)

            val zoomChanged = (prevCameraZoom.value - cameraZoom).absoluteValue > 0.01f
            val needsFullRebuild = showingMarkers != prevShowingMarkers.value ||
                showingToggleMarkers != prevShowingToggleMarkers.value ||
                routeResult != prevRouteResult.value ||
                zoomChanged
            val selectionChanged = selectedMarker != prevSelectedMarker.value
            val markerInfoChanged = selectedMarkerInfo != prevSelectedMarkerInfo.value
            val markerScale = MapParam.calculateMarkerScale(cameraZoom)

            // region Full rebuild (마커 리스트, 줌, 경로 변경 시)
            if (needsFullRebuild) {
                prevShowingMarkers.value = showingMarkers
                prevShowingToggleMarkers.value = showingToggleMarkers
                prevRouteResult.value = routeResult
                prevSelectedMarker.value = selectedMarker
                prevSelectedMarkerInfo.value = selectedMarkerInfo
                prevCameraZoom.value = cameraZoom

                mapView.clear()
                val newMap = mutableMapOf<String, GMSMarker>()
                var markerToSelect: GMSMarker? = null

                // Polyline
                routeResult?.let { route ->
                    if (route.pathPoints.isNotEmpty()) {
                        val path = GMSMutablePath()
                        route.pathPoints.forEach { point ->
                            val coordinate = cValue<CLLocationCoordinate2D> {
                                latitude = point.latitude
                                longitude = point.longitude
                            }
                            path.addCoordinate(coordinate)
                        }
                        val polyline = GMSPolyline.polylineWithPath(path)
                        polyline.strokeColor = MapParam.mainGreenColor
                        polyline.strokeWidth = 10.0 * markerScale
                        polyline.zIndex = 2
                        polyline.map = mapView
                    }
                }

                // Toggle 마커
                showingToggleMarkers.forEach { marker ->
                    val gmsMarker = GMSMarker()
                    val position = cValue<CLLocationCoordinate2D> {
                        latitude = marker.latitude
                        longitude = marker.longitude
                    }
                    gmsMarker.position = position
                    gmsMarker.userData = marker

                    when (marker.toggleType) {
                        MapToggle.CURB -> {
                            val size = 16.0 * markerScale
                            UIImage.imageNamed("ic_curb_marker")?.scaled(size, size)?.let { gmsMarker.icon = it }
                        }
                        MapToggle.SLOPE -> {
                            val size = 16.0 * markerScale
                            UIImage.imageNamed("ic_slope_marker")?.scaled(size, size)?.let { gmsMarker.icon = it }
                        }
                        MapToggle.STAIRS -> {
                            val size = 16.0 * markerScale
                            UIImage.imageNamed("ic_stairs_marker")?.scaled(size, size)?.let { gmsMarker.icon = it }
                        }
                        MapToggle.SPECIAL_MARK -> {
                            val isSelected = selectedMarker?.id == marker.id && selectedMarker is ToggleMarker
                            val baseSize = 20.0
                            val finalScale = if (isSelected) markerScale * 2.0f else markerScale
                            val size = baseSize * finalScale
                            val iconName = if (isSelected) "ic_special_selected" else "ic_special_marker"
                            UIImage.imageNamed(iconName)?.let {
                                val imgW = it.size.useContents { width }
                                val imgH = it.size.useContents { height }
                                val scaledHeight = if (imgW > 0) size * imgH / imgW else size
                                gmsMarker.icon = it.scaled(size, scaledHeight)
                            }
                            if (isSelected) {
                                gmsMarker.zIndex = Int.MAX_VALUE
                                if (selectedMarkerInfo is com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo) {
                                    markerToSelect = gmsMarker
                                }
                            }
                        }
                    }
                    gmsMarker.map = mapView
                    newMap["toggle_${marker.id}"] = gmsMarker
                }

                // 기본 마커
                showingMarkers.forEach { marker ->
                    when (marker) {
                        is BuildingMarker -> {
                            val gmsMarker = GMSMarker()
                            val position = cValue<CLLocationCoordinate2D> {
                                latitude = marker.latitude
                                longitude = marker.longitude
                            }
                            gmsMarker.position = position
                            gmsMarker.userData = marker
                            val isSelected = selectedMarker?.id == marker.id && selectedMarker is BuildingMarker
                            createBuildingMarkerView(marker.name, isSelected, markerScale).toUIImage()?.let {
                                gmsMarker.icon = it
                                gmsMarker.groundAnchor = cValue<CGPoint> { x = 0.5; y = 1.0 }
                            }
                            if (isSelected) gmsMarker.zIndex = Int.MAX_VALUE
                            gmsMarker.map = mapView
                            newMap["building_${marker.id}"] = gmsMarker
                        }
                        is DoorMarker -> {
                            val gmsMarker = GMSMarker()
                            val position = cValue<CLLocationCoordinate2D> {
                                latitude = marker.latitude
                                longitude = marker.longitude
                            }
                            gmsMarker.position = position
                            gmsMarker.userData = marker
                            createDoorMarkerView(marker.label, marker.isWheelChairAccessible, markerScale).toUIImage()?.let {
                                gmsMarker.icon = it
                            }
                            gmsMarker.map = mapView
                            newMap["door_${marker.id}"] = gmsMarker
                        }
                        is GateMarker -> {
                            val gmsMarker = GMSMarker()
                            val position = cValue<CLLocationCoordinate2D> {
                                latitude = marker.latitude
                                longitude = marker.longitude
                            }
                            gmsMarker.position = position
                            gmsMarker.userData = marker
                            val size = 30.0 * markerScale
                            UIImage.imageNamed("ic_gate_pin")?.scaled(size, size)?.let { gmsMarker.icon = it }
                            val isSelected = selectedMarker?.id == marker.id && selectedMarker is GateMarker
                            if (isSelected) {
                                gmsMarker.zIndex = Int.MAX_VALUE
                                if (selectedMarkerInfo is com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo) {
                                    markerToSelect = gmsMarker
                                }
                            }
                            gmsMarker.map = mapView
                            newMap["gate_${marker.id}"] = gmsMarker
                        }
                    }
                }

                gmsMarkerMap.value = newMap
                if (markerToSelect != null) {
                    mapView.selectedMarker = markerToSelect
                } else {
                    mapView.selectedMarker = null
                }
            }
            // endregion

            // region 선택 변경 (마커 아이콘만 교체, rebuild 없음)
            else if (selectionChanged) {
                val prevSel = prevSelectedMarker.value
                val newSel = selectedMarker
                prevSelectedMarker.value = selectedMarker
                prevSelectedMarkerInfo.value = selectedMarkerInfo
                val map = gmsMarkerMap.value

                // 이전 선택 해제
                if (prevSel != null) {
                    when (prevSel) {
                        is BuildingMarker -> {
                            map["building_${prevSel.id}"]?.let { gms ->
                                createBuildingMarkerView(prevSel.name, false, markerScale).toUIImage()?.let { gms.icon = it }
                                gms.zIndex = 0
                            }
                        }
                        is GateMarker -> {
                            map["gate_${prevSel.id}"]?.let { gms -> gms.zIndex = 0 }
                        }
                        is ToggleMarker -> if (prevSel.toggleType == MapToggle.SPECIAL_MARK) {
                            map["toggle_${prevSel.id}"]?.let { gms ->
                                val size = 20.0 * markerScale
                                UIImage.imageNamed("ic_special_marker")?.let {
                                    val imgW = it.size.useContents { width }
                                    val imgH = it.size.useContents { height }
                                    val scaledHeight = if (imgW > 0) size * imgH / imgW else size
                                    gms.icon = it.scaled(size, scaledHeight)
                                }
                                gms.zIndex = 0
                            }
                        }
                    }
                }

                // 새 선택 적용
                mapView.selectedMarker = null
                if (newSel != null) {
                    when (newSel) {
                        is BuildingMarker -> {
                            map["building_${newSel.id}"]?.let { gms ->
                                createBuildingMarkerView(newSel.name, true, markerScale).toUIImage()?.let { gms.icon = it }
                                gms.zIndex = Int.MAX_VALUE
                            }
                        }
                        is GateMarker -> {
                            map["gate_${newSel.id}"]?.let { gms ->
                                gms.zIndex = Int.MAX_VALUE
                                if (selectedMarkerInfo is com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo) {
                                    mapView.selectedMarker = gms
                                }
                            }
                        }
                        is ToggleMarker -> if (newSel.toggleType == MapToggle.SPECIAL_MARK) {
                            map["toggle_${newSel.id}"]?.let { gms ->
                                val finalScale = markerScale * 2.0f
                                val size = 20.0 * finalScale
                                UIImage.imageNamed("ic_special_selected")?.let {
                                    val imgW = it.size.useContents { width }
                                    val imgH = it.size.useContents { height }
                                    val scaledHeight = if (imgW > 0) size * imgH / imgW else size
                                    gms.icon = it.scaled(size, scaledHeight)
                                }
                                gms.zIndex = Int.MAX_VALUE
                                if (selectedMarkerInfo is com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo) {
                                    mapView.selectedMarker = gms
                                }
                            }
                        }
                    }
                }
            }
            // endregion

            // region InfoWindow만 갱신 (API 응답 도착 시)
            else if (markerInfoChanged) {
                prevSelectedMarkerInfo.value = selectedMarkerInfo
                val sel = selectedMarker
                val map = gmsMarkerMap.value
                if (selectedMarkerInfo != null && sel != null) {
                    val key = when (sel) {
                        is BuildingMarker -> "building_${sel.id}"
                        is GateMarker -> "gate_${sel.id}"
                        is ToggleMarker -> "toggle_${sel.id}"
                        else -> null
                    }
                    key?.let { map[it] }?.let { gms ->
                        mapView.selectedMarker = null
                        mapView.selectedMarker = gms
                    }
                } else {
                    mapView.selectedMarker = null
                }
            }
            // endregion

            // 카메라 위치 업데이트
            val latChanged = (lastSetCameraLat.value - cameraLatitude).absoluteValue > 0.00001
            val lngChanged = (lastSetCameraLng.value - cameraLongitude).absoluteValue > 0.00001
            val cameraZoomChanged = (lastSetCameraZoom.value - cameraZoom).absoluteValue > 0.01f
            if (latChanged || lngChanged || cameraZoomChanged) {
                val newCamera = GMSCameraPosition.cameraWithLatitude(
                    latitude = cameraLatitude,
                    longitude = cameraLongitude,
                    zoom = cameraZoom
                )
                mapView.camera = newCamera
                lastSetCameraLat.value = cameraLatitude
                lastSetCameraLng.value = cameraLongitude
                lastSetCameraZoom.value = cameraZoom
            }
        }
    )
}
