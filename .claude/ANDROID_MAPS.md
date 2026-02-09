# Android Google Maps 구현 가이드

KU-Barrier-Free-KMP 프로젝트의 Android Google Maps 구현을 상세히 정리한 문서입니다.

---

## 목차

1. [의존성 및 설정](#1-의존성-및-설정)
2. [LatLng 모델](#2-latlng-모델)
3. [MapComponent](#3-mapcomponent)
4. [마커 시스템](#4-마커-시스템)
5. [InfoWindow 시스템](#5-infowindow-시스템)
6. [폴리라인 및 경로](#6-폴리라인-및-경로)
7. [위치 유틸리티](#7-위치-유틸리티)
8. [HomeScreen 통합](#8-homescreen-통합)
9. [UI 컴포넌트](#9-ui-컴포넌트)
10. [파일 경로 요약](#10-파일-경로-요약)

---

## 1. 의존성 및 설정

### 1.1 Gradle 의존성

**파일:** `gradle/libs.versions.toml`

```toml
[versions]
mapsCompose = "4.4.1"
playServicesLocation = "21.3.0"
playServicesMaps = "19.2.0"

[libraries]
maps-compose = { module = "com.google.maps.android:maps-compose", version.ref = "mapsCompose" }
play-services-location = { module = "com.google.android.gms:play-services-location", version.ref = "playServicesLocation" }
play-services-maps = { module = "com.google.android.gms:play-services-maps", version.ref = "playServicesMaps" }
```

**파일:** `composeApp/build.gradle.kts`

```kotlin
androidMain.dependencies {
    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
}
```

### 1.2 API 키 설정

**파일:** `local.properties` (gitignore됨)

```properties
GOOGLE_MAPS_API_KEY=your_api_key_here
GOOGLE_MAPS_ID=your_map_id_here
BASE_URL=https://your.api.url
```

**파일:** `composeApp/build.gradle.kts`

```kotlin
val localProperties = Properties().apply {
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    defaultConfig {
        val mapsApiKey: String = localProperties.getProperty("GOOGLE_MAPS_API_KEY") ?: ""
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "GOOGLE_MAPS_ID",
            localProperties["GOOGLE_MAPS_ID"]?.toString() ?: "\"\"")
    }
}
```

### 1.3 AndroidManifest.xml 설정

**파일:** `composeApp/src/androidMain/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 권한 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application>
        <!-- Google Maps API 키 -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${GOOGLE_MAPS_API_KEY}" />
    </application>
</manifest>
```

---

## 2. LatLng 모델

### 2.1 Common 모델 (expect)

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/core/model/LatLng.kt`

```kotlin
@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

// 플랫폼별 LatLng 변환
expect fun LatLng.toPlatformLatLng(): Any
expect fun Any.toCommonLatLng(): LatLng
```

### 2.2 Android 구현 (actual)

**파일:** `composeApp/src/androidMain/kotlin/com/ganaljigi/kubf/core/model/LatLng.android.kt`

```kotlin
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

actual fun LatLng.toPlatformLatLng(): Any {
    return GoogleLatLng(latitude, longitude)
}

actual fun Any.toCommonLatLng(): LatLng {
    val googleLatLng = this as GoogleLatLng
    return LatLng(googleLatLng.latitude, googleLatLng.longitude)
}

// 편의를 위한 확장 함수
fun LatLng.toGoogleLatLng(): GoogleLatLng = GoogleLatLng(latitude, longitude)
fun GoogleLatLng.toCommon(): LatLng = LatLng(latitude, longitude)
```

---

## 3. MapComponent

### 3.1 Common 인터페이스 (expect)

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/component/map/MapComponent.kt`

```kotlin
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
```

### 3.2 Android 구현 (actual)

**파일:** `composeApp/src/androidMain/kotlin/com/ganaljigi/kubf/feature/home/component/map/MapComponent.kt`

#### 3.2.1 지도 파라미터 설정

```kotlin
object MapParam {
    const val MIN_ZOOM = 16.0f
    const val MAX_ZOOM = 18.0f

    val mapProperties = MapProperties(
        isBuildingEnabled = true,
        isIndoorEnabled = false,
        isMyLocationEnabled = false,
        isTrafficEnabled = false,
        // 건국대학교 캠퍼스 경계
        latLngBoundsForCameraTarget = LatLngBounds(
            LatLng(37.5373, 127.0656),  // 남서쪽
            LatLng(37.5450, 127.0952),  // 북동쪽
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
```

#### 3.2.2 MapComponent 구현

```kotlin
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
    onCameraMove: (Double, Double, Float) -> Unit,
    onBuildingMarkerClick: (BuildingMarker) -> Unit,
    onGateMarkerClick: (GateMarker) -> Unit,
    onSpecialMarkerClick: (ToggleMarker) -> Unit,
    onSpecialImageClick: (List<String>) -> Unit,
    onGateImageClick: (List<String>) -> Unit,
    onMapClick: () -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(cameraLatitude, cameraLongitude),
            cameraZoom
        )
    }

    // 줌 레벨에 따른 마커 스케일 계산
    val scale by remember {
        derivedStateOf {
            val zoom = cameraPositionState.position.zoom
            ((zoom - MapParam.MIN_ZOOM) / (MapParam.MAX_ZOOM - MapParam.MIN_ZOOM))
                .coerceIn(0f, 1f) * 0.5f + 0.5f
        }
    }

    // 카메라 이동 감지
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position }
            .collect { position ->
                onCameraMove(
                    position.target.latitude,
                    position.target.longitude,
                    position.zoom
                )
            }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapParam.mapProperties,
        uiSettings = MapParam.mapUiSettings,
        googleMapOptionsFactory = { MapParam.mapOptions },
        onMapClick = { onMapClick() },
    ) {
        // 경로 폴리라인 렌더링
        routeResult?.let { route ->
            if (route.pathPoints.isNotEmpty()) {
                Polyline(
                    points = route.pathPoints.map { it.toGoogleLatLng() },
                    color = MainGreen,
                    width = 10f,
                    zIndex = 2f,
                )
            }
        }

        // 마커 렌더링
        showingMarkers.forEach { marker ->
            when (marker) {
                is BuildingMarker -> BuildingMarkerComposable(
                    buildingMarker = marker,
                    isSelected = selectedMarker?.id == marker.id,
                    scale = scale,
                    onClick = onBuildingMarkerClick,
                )
                is DoorMarker -> DoorMarkerComposable(
                    doorMarker = marker,
                    scale = scale,
                )
                is GateMarker -> { /* GateMarker 렌더링 */ }
            }
        }

        // 토글 마커 렌더링
        showingToggleMarkers.forEach { toggleMarker ->
            when (toggleMarker.toggleType) {
                MapToggle.SPECIAL_MARK -> SpecialMarkerComposable(...)
                else -> ToggleMarkerComposable(...)
            }
        }
    }
}
```

---

## 4. 마커 시스템

### 4.1 마커 모델 구조

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/viewmodel/HomeUiState.kt`

```kotlin
// 모든 마커의 기본 인터페이스
interface MapMarker {
    val id: Long
    val latitude: Double
    val longitude: Double
}

// 선택 가능한 마커
interface SelectableMarker : MapMarker {
    val isSelected: Boolean
}

// 건물 마커
data class BuildingMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val name: String,
) : SelectableMarker

// 토글 마커 (연석, 경사로, 계단, 특이사항)
data class ToggleMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val toggleType: MapToggle,
) : SelectableMarker

// 출입문 마커
data class GateMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val name: String,
) : SelectableMarker

// 도어 마커 (선택 불가)
data class DoorMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    val label: String,
    val isWheelChairAccessible: Boolean = false,
) : MapMarker
```

### 4.2 MapToggle Enum

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/model/MapToggle.kt`

```kotlin
enum class MapToggle(
    val label: String,
    val toggleIconRes: DrawableResource,
    val markerIconRes: DrawableResource,
) {
    CURB("연석", ic_toggle_curb, ic_curb_marker),
    SLOPE("경사로", ic_toggle_slope, ic_slope_marker),
    STAIRS("계단", ic_toggle_stairs, ic_stairs_marker),
    SPECIAL_MARK("특이사항", ic_toggle_special, ic_special_marker),
}
```

### 4.3 BuildingMarkerComposable

**파일:** `composeApp/src/androidMain/kotlin/.../marker/BuildingMarkerComposable.kt`

```kotlin
@Composable
fun BuildingMarkerComposable(
    buildingMarker: BuildingMarker,
    isSelected: Boolean = false,
    scale: Float = 1f,
    onClick: (BuildingMarker) -> Unit = {},
) {
    val markerState = rememberMarkerState(
        key = buildingMarker.id.toString(),
        position = LatLng(buildingMarker.latitude, buildingMarker.longitude),
    )
    val iconScale = if (isSelected) scale * 2.0f else scale
    val iconSize = if (isSelected) (32.dp * iconScale) else (20.dp * iconScale)
    val fontSize = (14 * scale).coerceAtLeast(10f)

    MarkerComposable(
        onClick = { onClick(buildingMarker); false },
        state = markerState,
        zIndex = if (isSelected) Float.MAX_VALUE else 0f,
        keys = arrayOf(buildingMarker.id, isSelected, scale),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(
                    if (isSelected) R.drawable.ic_building_selected
                    else R.drawable.ic_building,
                ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.shadow(10.dp).size(iconSize),
            )
            // 외곽선이 있는 텍스트
            Box {
                Text(
                    text = buildingMarker.name,
                    style = typography.semiBold14.copy(
                        fontSize = fontSize.sp,
                        drawStyle = Stroke(width = 4f * scale),
                    ),
                    color = Color.White,
                )
                Text(
                    text = buildingMarker.name,
                    style = typography.semiBold14.copy(fontSize = fontSize.sp),
                    color = if (isSelected) MainGreen else Color(0xFF5A6860),
                )
            }
        }
    }
}
```

### 4.4 ToggleMarkerComposable

**파일:** `composeApp/src/androidMain/kotlin/.../marker/ToggleMarkerComposable.kt`

```kotlin
@Composable
fun ToggleMarkerComposable(
    toggleMarker: ToggleMarker,
    @DrawableRes toggleIconRes: Int,
    scale: Float = 1f,
) {
    val markerState = rememberMarkerState(
        key = toggleMarker.toString(),
        position = LatLng(toggleMarker.latitude, toggleMarker.longitude),
    )

    MarkerComposable(state = markerState) {
        Icon(
            painter = painterResource(toggleIconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(16.dp * scale).shadow(1.dp),
        )
    }
}
```

### 4.5 DoorMarkerComposable

**파일:** `composeApp/src/androidMain/kotlin/.../marker/DoorMarkerComposable.kt`

```kotlin
@Composable
fun DoorMarkerComposable(
    doorMarker: DoorMarker,
    scale: Float = 1f,
) {
    val fontSize = (14 * scale).coerceAtLeast(10f)

    MarkerComposable(
        state = MarkerState(
            position = LatLng(doorMarker.latitude, doorMarker.longitude),
        ),
        keys = arrayOf(doorMarker.id, scale),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (doorMarker.isWheelChairAccessible) MainGreen else Gray4,
                    shape = CircleShape,
                )
                .padding(horizontal = 4.dp * scale, vertical = 2.dp * scale)
                .sizeIn(minWidth = 16.dp * scale, minHeight = 16.dp * scale),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = doorMarker.label,
                style = typography.medium14.copy(
                    fontSize = fontSize.sp,
                    color = Color.White,
                ),
            )
        }
    }
}
```

### 4.6 SpecialMarkerComposable

**파일:** `composeApp/src/androidMain/kotlin/.../marker/SpecialMarkerComposable.kt`

```kotlin
@Composable
fun SpecialMarkerComposable(
    markerState: MarkerState,
    isSelected: Boolean = false,
    specialMarkerInfo: SpecialMarkerInfo?,
    onSpecialMarkerClick: () -> Unit = {},
    onSpecialInfoClick: (List<String>) -> Unit = {},
    scale: Float = 1f,
) {
    val iconScale = if (isSelected) scale * 2.0f else scale
    val imageUrls = specialMarkerInfo?.imageUrls?.take(2) ?: emptyList()

    // 이미지 로딩 상태 관리
    val isImageLoaded = remember(imageUrls, isSelected) {
        mutableStateListOf(*Array(imageUrls.size) { false })
    }
    val painters = imageUrls.mapIndexed { index, imageUrl ->
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .allowHardware(false)
                .build(),
            onSuccess = { isImageLoaded[index] = true },
            onError = { isImageLoaded[index] = true },
        )
    }
    val allImagesLoaded by remember {
        derivedStateOf { isImageLoaded.all { it } }
    }

    // 이미지 로딩 완료 시 InfoWindow 표시
    LaunchedEffect(specialMarkerInfo, allImagesLoaded) {
        if (isSelected && allImagesLoaded) {
            markerState.showInfoWindow()
        } else if (!isSelected) {
            markerState.hideInfoWindow()
        }
    }

    MarkerComposableInfoWindow(
        markerState = markerState,
        isSelected = isSelected,
        specialMarkerInfo = specialMarkerInfo,
        onSpecialMarkerClick = onSpecialMarkerClick,
        onSpecialInfoClick = onSpecialInfoClick,
        painters = painters,
    ) {
        Icon(
            painter = painterResource(
                if (isSelected) R.drawable.ic_special_marker_selected
                else R.drawable.ic_special_marker,
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp * iconScale),
        )
    }
}
```

### 4.7 GateMarkerComposable

**파일:** `composeApp/src/androidMain/kotlin/.../marker/GateMarkerComposable.kt`

```kotlin
@Composable
fun GateMarkerComposable(
    markerState: MarkerState,
    isSelected: Boolean = false,
    gateMarkerInfo: GateMarkerInfo?,
    onGateMarkerClick: () -> Unit = {},
    onGateInfoClick: (List<String>) -> Unit = {},
    scale: Float = 1f,
) {
    // SpecialMarkerComposable과 유사한 구조
    // 이미지 로딩 및 InfoWindow 관리

    GateMarkerInfoWindow(
        markerState = markerState,
        isSelected = isSelected,
        gateMarkerInfo = gateMarkerInfo,
        onGateMarkerClick = onGateMarkerClick,
        onGateInfoClick = onGateInfoClick,
        painters = painters,
        scale = scale,
    )
}
```

---

## 5. InfoWindow 시스템

### 5.1 Composable을 BitmapDescriptor로 변환

**파일:** `composeApp/src/androidMain/kotlin/.../map/rememberComposeBitmapDescriptor.kt`

```kotlin
@Composable
internal fun rememberComposeBitmapDescriptor(
    vararg keys: Any,
    content: @Composable () -> Unit,
): BitmapDescriptor {
    val parent = LocalView.current as ViewGroup
    val compositionContext = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    return remember(parent, compositionContext, currentContent, *keys) {
        renderComposableToBitmapDescriptor(parent, compositionContext, currentContent)
    }
}

private fun renderComposableToBitmapDescriptor(
    parent: ViewGroup,
    compositionContext: CompositionContext,
    content: @Composable () -> Unit,
): BitmapDescriptor {
    val composeView = ComposeView(parent.context).apply {
        setParentCompositionContext(compositionContext)
        setContent(content)
    }.also(parent::addView)

    // 측정 및 레이아웃
    composeView.measure(
        View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.AT_MOST),
        View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.AT_MOST),
    )
    composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

    // 비트맵 생성
    val bitmap = Bitmap.createBitmap(
        composeView.measuredWidth,
        composeView.measuredHeight,
        Bitmap.Config.ARGB_8888,
    )
    bitmap.applyCanvas { composeView.draw(this) }
    parent.removeView(composeView)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
```

### 5.2 MarkerComposableInfoWindow

**파일:** `composeApp/src/androidMain/kotlin/.../infowindow/MarkerComposableInfoWindow.kt`

```kotlin
@Composable
fun MarkerComposableInfoWindow(
    markerState: MarkerState,
    isSelected: Boolean,
    specialMarkerInfo: SpecialMarkerInfo,
    onSpecialMarkerClick: () -> Unit = {},
    onSpecialInfoClick: (List<String>) -> Unit = {},
    painters: List<Painter> = emptyList(),
    content: @Composable () -> Unit,
) {
    val icon = rememberComposeBitmapDescriptor(isSelected) { content() }

    MarkerInfoWindow(
        state = markerState,
        onClick = {
            onSpecialMarkerClick()
            false
        },
        onInfoWindowClick = {
            onSpecialInfoClick(specialMarkerInfo.imageUrls)
        },
        icon = icon,
        infoWindowAnchor = Offset(0.5f, -0.2f),
    ) {
        MapSpecialInfo(
            painters = painters,
            description = specialMarkerInfo.description,
            modifier = Modifier.noRippleClickable {
                onSpecialInfoClick(specialMarkerInfo.imageUrls)
            },
        )
    }
}
```

### 5.3 MapSpecialInfo (InfoWindow 컨텐츠)

**파일:** `composeApp/src/androidMain/kotlin/.../infowindow/MapSpecialInfo.kt`

```kotlin
@Composable
fun MapSpecialInfo(
    description: String,
    modifier: Modifier = Modifier,
    painters: List<Painter>,
    scale: Float = 1f,
) {
    val cornerRadius by remember(scale) { mutableStateOf(20.dp * scale) }
    val imageSize by remember(scale) { mutableStateOf(80.dp * scale) }
    val fontSize by remember(scale) { mutableFloatStateOf((14 * scale).coerceAtLeast(10f)) }

    Column(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(cornerRadius))
            .border(1.dp, Gray2.copy(alpha = 0.5f), RoundedCornerShape(cornerRadius))
            .padding(12.dp * scale)
            .widthIn(max = 214.dp * scale),
        verticalArrangement = Arrangement.spacedBy(16.dp * scale),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp * scale)) {
            painters.forEach { painter ->
                Image(
                    painter = painter,
                    contentDescription = description,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(10.dp * scale)),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Text(
            text = description,
            style = typography.semiBold14.copy(
                fontSize = fontSize.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}
```

### 5.4 MapGateInfo

**파일:** `composeApp/src/androidMain/kotlin/.../infowindow/MapGateInfo.kt`

```kotlin
@Composable
fun MapGateInfo(
    description: String,
    modifier: Modifier = Modifier,
    painters: List<Painter>,
    scale: Float = 1f,
) {
    // MapSpecialInfo와 유사하지만 더 큰 이미지 사이즈 (160.dp)
    val imageSize by remember(scale) { mutableStateOf(160.dp * scale) }
    val fontSize by remember(scale) { mutableFloatStateOf((21 * scale).coerceAtLeast(10f)) }
    // ...
}
```

---

## 6. 폴리라인 및 경로

### 6.1 RouteResult 모델

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/model/RouteResult.kt`

```kotlin
data class RouteResult(
    val time: Int = 0,
    val distance: Int = 0,
    val routeMode: RouteMode = RouteMode.SHORTEST,
    val pathPoints: ImmutableList<LatLng> = persistentListOf(),
    val distanceText: String = "",
)

enum class RouteMode {
    SHORTEST,
    BARRIER_FREE,
}

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val index: Int,
)

data class RouteLine(
    val points: List<LatLng>,
    val type: String,
    val index: Int,
)
```

### 6.2 폴리라인 렌더링

**MapComponent.kt 내부:**

```kotlin
// 선택된 경로 렌더링
routeResult?.let { route ->
    if (route.pathPoints.isNotEmpty()) {
        Polyline(
            points = route.pathPoints.map { it.toGoogleLatLng() },
            color = MainGreen,  // 메인 녹색
            width = 10f,
            zIndex = 2f,
        )
    }
}
```

---

## 7. 위치 유틸리티

### 7.1 Common 인터페이스 (expect)

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/core/ui/util/LocationUtil.kt`

```kotlin
expect suspend fun PermissionsController.ensureLocationPermission(): Boolean

expect suspend fun getLocationWithPermission(
    permissionsController: PermissionsController,
): Pair<Double, Double>?
```

### 7.2 Android 구현 (actual)

**파일:** `composeApp/src/androidMain/kotlin/com/ganaljigi/kubf/core/ui/util/LocationUtil.android.kt`

```kotlin
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController

// 위치 권한 요청
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

// 위치 권한 확인
suspend fun PermissionsController.hasLocationPermission(): Boolean {
    return isPermissionGranted(Permission.LOCATION)
}

// 위치 권한 보장
actual suspend fun PermissionsController.ensureLocationPermission(): Boolean {
    return if (hasLocationPermission()) {
        true
    } else {
        requestLocationPermission()
    }
}

// 현재 위치 가져오기
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

// 권한 확인 후 위치 가져오기
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
```

---

## 8. HomeScreen 통합

### 8.1 HomeScreen (Android)

**파일:** `composeApp/src/androidMain/kotlin/com/ganaljigi/kubf/feature/home/screen/HomeScreen.kt`

```kotlin
@Composable
actual fun HomeRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel,
    onNavigateToBuildingInfo: (Int) -> Unit,
    onNavigateToHelper: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionsController = rememberPermissionsControllerFactory()
        .createPermissionsController()

    // 카메라 상태
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(uiState.cameraLatitude, uiState.cameraLongitude),
            uiState.cameraZoom
        )
    }

    // 바텀시트 상태
    val bottomSheetState = rememberBottomSheetScaffoldState()

    // 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.MoveCamera -> {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(event.latitude, event.longitude),
                                event.zoom,
                            ),
                        )
                    }
                }
                HomeUiEvent.RequestMyLocation -> {
                    scope.launch {
                        getLocationWithPermission(context, permissionsController)?.let { location ->
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(location, 17f),
                            )
                        }
                    }
                }
                is HomeUiEvent.SetBottomSheetExpanded -> {
                    scope.launch {
                        if (event.expanded) bottomSheetState.bottomSheetState.expand()
                        else bottomSheetState.bottomSheetState.hide()
                    }
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = { /* 바텀시트 컨텐츠 */ },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapComponent(
                modifier = Modifier.fillMaxSize(),
                cameraLatitude = uiState.cameraLatitude,
                cameraLongitude = uiState.cameraLongitude,
                cameraZoom = uiState.cameraZoom,
                selectedMarker = uiState.selectedMarker,
                selectedMarkerInfo = uiState.selectedMarkerInfo,
                showingMarkers = uiState.showingMarkers,
                showingToggleMarkers = uiState.showingToggleMarkers,
                routeResult = uiState.selectedRoute,
                onCameraMove = { lat, lng, zoom ->
                    viewModel.onAction(HomeUiAction.UpdateCamera(lat, lng, zoom))
                },
                onBuildingMarkerClick = { marker ->
                    viewModel.onAction(HomeUiAction.SelectBuildingMarker(marker))
                },
                onMapClick = {
                    viewModel.onAction(HomeUiAction.DeselectMarker)
                },
            )

            // 토글 버튼들
            HomeToggle(
                toggleUiStates = uiState.toggleUiStates,
                onToggleClick = { toggle ->
                    viewModel.onAction(HomeUiAction.ToggleMapToggle(toggle))
                },
            )

            // 내 위치 버튼
            MyLocationButton(
                onClick = { viewModel.onAction(HomeUiAction.RequestMyLocation) }
            )
        }
    }
}
```

---

## 9. UI 컴포넌트

### 9.1 HomeToggle

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/component/HomeToggle.kt`

```kotlin
@Composable
fun HomeToggle(
    modifier: Modifier = Modifier,
    toggleUiStates: List<ToggleUiState>,
    onToggleClick: (MapToggle) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        toggleUiStates.forEach { state ->
            HomeToggleChip(
                isSelected = state.isSelected,
                toggle = state.toggle,
                onToggleClick = { onToggleClick(state.toggle) },
            )
        }
    }
}

@Composable
fun HomeToggleChip(
    isSelected: Boolean = false,
    toggle: MapToggle,
    onToggleClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.noRippleClickable { onToggleClick() },
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) LightGreen else Color.White,
        border = if (isSelected) BorderStroke(1.dp, MainGreen) else null,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 32.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(toggle.toggleIconRes),
                contentDescription = toggle.label,
                tint = Color.Unspecified,
            )
            Text(
                text = toggle.label,
                style = typography.medium13.copy(
                    color = if (isSelected) MainGreen else Color.Black,
                ),
            )
        }
    }
}
```

### 9.2 MyLocationButton

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/component/MyLocationButton.kt`

```kotlin
@Composable
fun MyLocationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.noRippleClickable { onClick() },
        shadowElevation = 4.dp,
        shape = CircleShape,
        color = Color.White,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_my_location),
            contentDescription = "내 위치",
            modifier = Modifier.padding(12.dp),
            tint = MainGreen,
        )
    }
}
```

### 9.3 FindWayButton

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/component/FindWayButton.kt`

```kotlin
@Composable
fun FindWayButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.noRippleClickable { onClick() },
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        color = MainGreen,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_find_way),
                contentDescription = null,
                tint = Color.White,
            )
            Text(
                text = "길찾기",
                style = typography.semiBold14.copy(color = Color.White),
            )
        }
    }
}
```

### 9.4 HomeRouteInfo

**파일:** `composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/feature/home/component/find/HomeFindRouteComponent.kt`

```kotlin
@Composable
fun HomeRouteInfo(
    modifier: Modifier = Modifier,
    selectedRoute: RouteResult?,
    routeResults: List<RouteResult>,
    onRouteSelected: (RouteResult) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RouteMode.entries.forEach { routeMode ->
            routeResults.find { it.routeMode == routeMode }?.let { route ->
                HomeRouteInfoItem(
                    modifier = Modifier.weight(1f),
                    routeResult = route,
                    isSelected = selectedRoute?.routeMode == routeMode,
                    onClick = onRouteSelected,
                )
            }
        }
    }
}
```

---

## 10. 파일 경로 요약

| 구분 | 파일 경로 |
|------|----------|
| **설정** | |
| Gradle 의존성 | `gradle/libs.versions.toml` |
| 빌드 설정 | `composeApp/build.gradle.kts` |
| AndroidManifest | `composeApp/src/androidMain/AndroidManifest.xml` |
| **모델** | |
| LatLng (expect) | `composeApp/src/commonMain/.../core/model/LatLng.kt` |
| LatLng (actual) | `composeApp/src/androidMain/.../core/model/LatLng.android.kt` |
| MapToggle | `composeApp/src/commonMain/.../feature/home/model/MapToggle.kt` |
| RouteResult | `composeApp/src/commonMain/.../feature/home/model/RouteResult.kt` |
| **MapComponent** | |
| MapComponent (expect) | `composeApp/src/commonMain/.../feature/home/component/map/MapComponent.kt` |
| MapComponent (actual) | `composeApp/src/androidMain/.../feature/home/component/map/MapComponent.kt` |
| **마커** | |
| BuildingMarkerComposable | `composeApp/src/androidMain/.../map/marker/BuildingMarkerComposable.kt` |
| ToggleMarkerComposable | `composeApp/src/androidMain/.../map/marker/ToggleMarkerComposable.kt` |
| SpecialMarkerComposable | `composeApp/src/androidMain/.../map/marker/SpecialMarkerComposable.kt` |
| GateMarkerComposable | `composeApp/src/androidMain/.../map/marker/GateMarkerComposable.kt` |
| DoorMarkerComposable | `composeApp/src/androidMain/.../map/marker/DoorMarkerComposable.kt` |
| **InfoWindow** | |
| rememberComposeBitmapDescriptor | `composeApp/src/androidMain/.../map/rememberComposeBitmapDescriptor.kt` |
| MarkerComposableInfoWindow | `composeApp/src/androidMain/.../map/infowindow/MarkerComposableInfoWindow.kt` |
| GateMarkerInfoWindow | `composeApp/src/androidMain/.../map/infowindow/GateMarkerInfoWindow.kt` |
| MapSpecialInfo | `composeApp/src/androidMain/.../map/infowindow/MapSpecialInfo.kt` |
| MapGateInfo | `composeApp/src/androidMain/.../map/infowindow/MapGateInfo.kt` |
| **위치** | |
| LocationUtil (expect) | `composeApp/src/commonMain/.../core/ui/util/LocationUtil.kt` |
| LocationUtil (actual) | `composeApp/src/androidMain/.../core/ui/util/LocationUtil.android.kt` |
| **화면** | |
| HomeScreen (actual) | `composeApp/src/androidMain/.../feature/home/screen/HomeScreen.kt` |
| **UI 컴포넌트** | |
| HomeToggle | `composeApp/src/commonMain/.../feature/home/component/HomeToggle.kt` |
| MyLocationButton | `composeApp/src/commonMain/.../feature/home/component/MyLocationButton.kt` |
| FindWayButton | `composeApp/src/commonMain/.../feature/home/component/FindWayButton.kt` |

---

## 아키텍처 요약

### 데이터 흐름

```
ViewModel (상태 관리)
    ↓
HomeScreen (이벤트 수집 & 전달)
    ↓
MapComponent (지도 렌더링)
    ↓
Marker Composables (마커 표시)
    ↓
InfoWindow (정보창 표시)
```

### 마커 타입 계층

```
MapMarker (기본 인터페이스)
├── SelectableMarker (선택 가능)
│   ├── BuildingMarker (건물)
│   ├── ToggleMarker (토글 - 연석/경사로/계단/특이사항)
│   └── GateMarker (출입문)
└── DoorMarker (도어 - 선택 불가)
```

### 핵심 기술 스택

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Google Maps Compose | 4.4.1 | 지도 UI |
| Play Services Maps | 19.2.0 | 지도 API |
| Play Services Location | 21.3.0 | 위치 서비스 |
| Moko Permissions | - | 권한 관리 |
| Coil | 3.1.0 | 이미지 로딩 |

---

## 11. iOS 구현 파일

| 구분 | 파일 경로 |
|------|----------|
| **MapComponent** | `composeApp/src/iosMain/.../feature/home/component/map/MapComponent.ios.kt` |
| **MarkerViewRenderer** | `composeApp/src/iosMain/.../feature/home/component/map/MarkerViewRenderer.ios.kt` |
| **HomeScreen** | `composeApp/src/iosMain/.../feature/home/screen/HomeScreen.ios.kt` |
| **LocationUtil** | `composeApp/src/iosMain/.../core/ui/util/LocationUtil.ios.kt` |
| **LatLng** | `composeApp/src/iosMain/.../core/model/LatLng.ios.kt` |

---

## 12. iOS 구현 특이사항

### GMSMapViewDelegateProtocol 메서드

| 메서드 | 용도 |
|--------|------|
| `didTapMarker` | 마커 클릭 처리 |
| `didTapAtCoordinate` | 지도 클릭 (빈 공간) |
| `markerInfoWindow` | 커스텀 InfoWindow 반환 |
| `didTapInfoWindowOfMarker` | InfoWindow 클릭 |
| `idleAtCameraPosition` | 카메라 이동 완료 |

### UIView → UIImage 변환

iOS에서는 Composable을 직접 비트맵으로 변환할 수 없으므로
`MarkerViewRenderer.ios.kt`에서 UIView를 생성 후 `toUIImage()`로 변환

```kotlin
// UIImage 크기 조절
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
```

### InfoWindow 표시 방식

| 플랫폼 | 방식 |
|--------|------|
| Android | `MarkerInfoWindow` Composable + BitmapDescriptor |
| iOS | delegate의 `markerInfoWindow` 반환값으로 UIView 제공 |

### 위치 권한 처리

iOS에서는 Moko Permissions를 사용하여 위치 권한 요청:

```kotlin
// PermissionsController 초기화
val permissionsControllerFactory = rememberPermissionsControllerFactory()
val permissionsController = remember(permissionsControllerFactory) {
    permissionsControllerFactory.createPermissionsController()
}

// 위치 가져오기
scope.launch {
    getLocationWithPermission(permissionsController)?.let { (lat, lng) ->
        cameraLatitude = lat
        cameraLongitude = lng
    }
}
```

### 카메라 이동 추적

```kotlin
// GMSMapViewDelegateProtocol 내부
@ObjCSignatureOverride
override fun mapView(mapView: GMSMapView, idleAtCameraPosition: GMSCameraPosition) {
    onCameraMove(
        idleAtCameraPosition.target.latitude,
        idleAtCameraPosition.target.longitude,
        idleAtCameraPosition.zoom
    )
}
```

---

## Android-iOS 기능 비교표

| 기능 | Android | iOS |
|------|---------|-----|
| 지도 라이브러리 | Google Maps Compose | Google Maps SDK (CocoaPods) |
| 마커 렌더링 | MarkerComposable | GMSMarker + UIImage |
| InfoWindow | MarkerInfoWindow | GMSMapViewDelegate |
| 카메라 이동 추적 | LaunchedEffect + snapshotFlow | idleAtCameraPosition delegate |
| 위치 서비스 | FusedLocationProviderClient | CLLocationManager |
| 권한 관리 | Moko Permissions | Moko Permissions |
| Polyline | Polyline Composable | GMSPolyline |
| 카메라 경계 제한 | LatLngBounds | GMSCoordinateBounds |
