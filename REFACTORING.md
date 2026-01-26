# KU-Barrier-Free KMP 리팩토링 가이드

> 분석 일자: 2026-01-26
> 프로젝트: KU-Barrier-Free-KMP (쿠맵)

---

## 목차
1. [긴급 수정 사항](#1-긴급-수정-사항)
2. [코드 품질 개선](#2-코드-품질-개선)
3. [아키텍처 개선](#3-아키텍처-개선)
4. [리소스 관리](#4-리소스-관리)
5. [테스트 코드](#5-테스트-코드)
6. [에러 핸들링](#6-에러-핸들링)
7. [성능 최적화](#7-성능-최적화)

---

## 1. 긴급 수정 사항

### 1.1 패키지명 불일치 (Critical)
```
현재 상태:
- build.gradle.kts namespace: com.ganalijigi.kubf
- 실제 패키지: com.ganaljigi.kubf
```

**수정 방안**: 하나로 통일 필요 (`ganaljigi` vs `ganalijigi` 철자 확인)

### 1.2 미완료 TODO 항목 (6개)
| 파일 | 라인 | 내용 |
|------|------|------|
| `HomeScreen.kt` | 147 | `// TODO: 검색 바텀시트` |
| `HomeViewModel.kt` | 334 | `// TODO: 에러 처리` |
| `BuildingInfoScreen.kt` | 89-95 | 주석 처리된 LaunchedEffect |

### 1.3 Dummy 모듈 제거
- `DummyScreen`, `DummyViewModel`, `DummyRepository` 등 프로덕션 빌드에서 제외 필요
- 위치: `/app/src/main/java/com/ganaljigi/kubf/dummy/`

---

## 2. 코드 품질 개선

### 2.1 중복 코드 제거

#### DateTimeFormatter 중복 (`HelperViewModel.kt`)
```kotlin
// Before (라인 36-38, 47-49 중복)
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val parsed = LocalDate.parse(dateString, formatter)

// After - 유틸리티 함수로 추출
object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseDate(dateString: String): LocalDate =
        LocalDate.parse(dateString, dateFormatter)
}
```

#### 마커 업데이트 로직 (`HomeViewModel.kt`)
```kotlin
// 라인 324, 620에서 반복되는 패턴
// Before
_uiState.update {
    it.copy(
        showingDoorMarkers = ...,
        buildingInfo = ...
    )
}

// After - 함수로 추출
private fun updateMarkerState(doorMarkers: List<DoorMarker>, buildingInfo: BuildingInfo?) {
    _uiState.update {
        it.copy(showingDoorMarkers = doorMarkers, buildingInfo = buildingInfo)
    }
}
```

### 2.2 하드코딩 값 상수화

#### 좌표 상수화 (`HomeUiState.kt`)
```kotlin
// Before
position = CameraPosition.fromLatLngZoom(LatLng(37.5407, 127.0785), 17f)

// After
object MapConstants {
    val KONKUK_UNIVERSITY = LatLng(37.5407, 127.0785)
    const val DEFAULT_ZOOM = 17f
}
```

#### API 응답 코드 (`BaseResponse.kt`)
```kotlin
// Before
when (this.code) {
    200, 1000 -> Result.success(this.result)
}

// After
object ApiResponseCode {
    const val HTTP_OK = 200
    const val API_SUCCESS = 1000
    val SUCCESS_CODES = listOf(HTTP_OK, API_SUCCESS)
}
```

#### 문자열 상수화
```kotlin
// Before (HomeViewModel.kt 라인 290-292)
when (type) {
    "BUILDING" -> ...
    "FACILITY" -> ...
}

// After
enum class PlaceType(val value: String) {
    BUILDING("BUILDING"),
    FACILITY("FACILITY")
}
```

### 2.3 긴 함수 분할

#### `HomeViewModel.kt` (655줄) 분할 제안
```
HomeViewModel (현재 655줄)
├── HomeViewModel (200줄) - 상태 관리 + Coordinator
├── RouteManager (150줄) - 경로 계산 로직
├── SearchManager (150줄) - 검색 관련 로직
└── MarkerManager (150줄) - 마커 관리 로직
```

### 2.4 매개변수 그룹화

#### `MapComponent` 파라미터 정리
```kotlin
// Before - 12개 이상의 매개변수
@Composable
fun MapComponent(
    cameraPositionState: CameraPositionState,
    buildingMarkers: List<BuildingMarker>,
    toggleMarkers: List<ToggleMarker>,
    doorMarkers: List<DoorMarker>,
    polyline: List<LatLng>,
    onMapClick: () -> Unit,
    // ... 더 많은 매개변수
)

// After - 데이터 클래스로 그룹화
data class MapComponentState(
    val cameraPositionState: CameraPositionState,
    val markers: MarkerState,
    val polyline: List<LatLng>,
    val callbacks: MapCallbacks
)

data class MarkerState(
    val buildings: List<BuildingMarker>,
    val toggles: List<ToggleMarker>,
    val doors: List<DoorMarker>
)

data class MapCallbacks(
    val onMapClick: () -> Unit,
    val onMarkerClick: (BuildingMarker) -> Unit,
    val onCameraMove: () -> Unit
)
```

---

## 3. 아키텍처 개선

### 3.1 DI 모듈 통합
```
현재 구조 (분산됨):
├── NetworkModule
├── ApiModule
├── RepositoryModule
├── BuildingInfoRepositoryModule
├── HelperRepositoryModule
└── RoomInfoRepositoryModule

권장 구조:
├── NetworkModule (Retrofit, OkHttp)
├── RepositoryModule (모든 Repository 바인딩)
└── TestModule (테스트용 Mock 바인딩)
```

### 3.2 Repository 패턴 일관성
```kotlin
// 인터페이스 통일
interface Repository<T> {
    suspend fun getAll(): Result<List<T>>
    suspend fun getById(id: Long): Result<T>
}

// Base 구현
abstract class BaseRepository<T, D : Dto>(
    private val service: ApiService
) : Repository<T> {
    abstract fun D.toDomain(): T
}
```

### 3.3 에러 핸들링 계층화
```kotlin
// Domain Layer
sealed class DomainError {
    data class Network(val cause: Throwable) : DomainError()
    data class Api(val code: Int, val message: String) : DomainError()
    object NotFound : DomainError()
}

// UI Layer
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: DomainError) : UiState<Nothing>()
}
```

---

## 4. 리소스 관리

### 4.1 문자열 리소스화
**대상 파일들에서 하드코딩된 한글 문자열 추출 필요**

```xml
<!-- strings.xml에 추가 필요 -->
<string name="error_default">오류가 발생했습니다</string>
<string name="error_no_data">데이터가 없습니다</string>
<string name="building_type">건물</string>
<string name="facility_type">편의시설</string>
<string name="search_result_empty">검색 결과가 없습니다</string>
```

### 4.2 색상 리소스 정리
```xml
<!-- colors.xml 정리 - 레거시 색상 제거 후 실제 사용 색상 추가 -->
<color name="main_green">#00B85F</color>
<color name="gray_3">#888888</color>
<color name="gray_4">#CCCCCC</color>
<!-- 기존 purple_* 색상은 제거 -->
```

### 4.3 Dimension 리소스 추가
```xml
<!-- dimens.xml 신규 생성 -->
<dimen name="spacing_small">8dp</dimen>
<dimen name="spacing_medium">16dp</dimen>
<dimen name="spacing_large">24dp</dimen>
<dimen name="corner_radius_default">8dp</dimen>
<dimen name="marker_size">48dp</dimen>
```

---

## 5. 테스트 코드

### 5.1 테스트 현황
| 유형 | 현재 | 목표 |
|------|------|------|
| Unit Test | 1개 (예시) | ViewModel 당 최소 5개 |
| Integration Test | 0개 | Repository 당 최소 3개 |
| UI Test | 0개 | 주요 Screen 당 최소 2개 |

### 5.2 우선 작성 대상

#### ViewModel 테스트
```kotlin
// HomeViewModelTest.kt
class HomeViewModelTest {
    @Test
    fun `건물 검색 시 결과가 UI State에 반영된다`()

    @Test
    fun `경로 요청 실패 시 에러 상태가 반영된다`()

    @Test
    fun `마커 클릭 시 해당 건물 정보가 로드된다`()
}
```

#### Repository 테스트
```kotlin
// HomeRepositoryTest.kt
class HomeRepositoryTest {
    @Test
    fun `API 성공 응답 시 Result.success 반환`()

    @Test
    fun `API 실패 응답 시 Result.failure 반환`()
}
```

### 5.3 테스트 의존성 추가
```kotlin
// build.gradle.kts
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
testImplementation("io.mockk:mockk:1.13.10")
testImplementation("app.cash.turbine:turbine:1.0.0") // Flow 테스트
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## 6. 에러 핸들링

### 6.1 현재 문제점
- 에러 발생 시 `Log.e()`만 호출하고 UI에 반영 안 됨
- 네트워크 에러와 API 에러 구분 없음
- 사용자에게 일반적인 "오류" 메시지만 표시

### 6.2 개선 방안

#### 에러 타입 정의
```kotlin
sealed class AppError {
    object NetworkError : AppError()
    object ServerError : AppError()
    data class ApiError(val code: Int, val message: String) : AppError()
    object UnknownError : AppError()
}
```

#### BaseResponse 개선
```kotlin
fun <T> BaseResponse<T>.toResult(): Result<T> = when {
    code in ApiResponseCode.SUCCESS_CODES -> Result.success(result)
    code in 400..499 -> Result.failure(ClientException(code, message))
    code in 500..599 -> Result.failure(ServerException(code, message))
    else -> Result.failure(UnknownException(message))
}
```

#### UI State 에러 표시
```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: AppError? = null,  // 에러 상태 추가
    val showErrorDialog: Boolean = false
)
```

---

## 7. 성능 최적화

### 7.1 Compose 최적화

#### remember 활용
```kotlin
// Before
@Composable
fun MarkerList(markers: List<Marker>) {
    val sortedMarkers = markers.sortedBy { it.name }  // 매 recomposition마다 실행
}

// After
@Composable
fun MarkerList(markers: List<Marker>) {
    val sortedMarkers = remember(markers) { markers.sortedBy { it.name } }
}
```

#### derivedStateOf 활용
```kotlin
// 자주 변경되는 state에서 파생된 값
val filteredItems by remember {
    derivedStateOf { items.filter { it.isVisible } }
}
```

### 7.2 Flow 최적화
```kotlin
// Before - 모든 emit에 collect
homeData.collect { data -> updateUI(data) }

// After - distinctUntilChanged로 중복 방지
homeData
    .distinctUntilChanged()
    .collect { data -> updateUI(data) }
```

### 7.3 이미지 로딩 최적화
```kotlin
// Coil 캐시 설정
ImageLoader.Builder(context)
    .memoryCache {
        MemoryCache.Builder(context)
            .maxSizePercent(0.25)
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizePercent(0.02)
            .build()
    }
    .build()
```

---

## 우선순위별 작업 목록

### Phase 1 - 긴급 (1주 내)
- [ ] 패키지명 통일
- [ ] TODO 항목 해결
- [ ] Dummy 모듈 분리/제거

### Phase 2 - 단기 (2주 내)
- [ ] 중복 코드 제거 (DateUtils, MarkerManager)
- [ ] 하드코딩 값 상수화
- [ ] 문자열 리소스화

### Phase 3 - 중기 (1개월 내)
- [ ] HomeViewModel 분할
- [ ] 에러 핸들링 계층화
- [ ] 기본 테스트 코드 작성

### Phase 4 - 장기 (분기 내)
- [ ] DI 모듈 재구성
- [ ] 성능 최적화
- [ ] 테스트 커버리지 70% 달성

---

## 코드 메트릭 요약

| 항목 | 수치 |
|------|------|
| 총 Kotlin 파일 | 146개 |
| 총 코드 라인 | ~12,000줄 |
| ViewModel | 4개 |
| Repository | 6개 |
| Screen | 9개 |
| Component | 19개 |
| 테스트 커버리지 | ~0% |

---

*이 문서는 지속적으로 업데이트되어야 합니다.*
