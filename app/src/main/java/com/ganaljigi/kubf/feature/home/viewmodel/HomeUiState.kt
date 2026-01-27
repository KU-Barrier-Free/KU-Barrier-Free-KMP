package com.ganaljigi.kubf.feature.home.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ganaljigi.kubf.core.model.Convenience
import com.ganaljigi.kubf.core.model.DoorInfo
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.model.SearchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

data class HomeUiState(
    // 상단 영역
    val fromLocation: SearchResult? = null,
    val toLocation: SearchResult? = null,
    val toggleStates: ImmutableList<ToggleUiState> = ToggleUiState.defaults.toPersistentList(),

    // 검색 화면
    val isSearchScreenShown: Boolean = false,
    val searchText: String = "",
    val popularKeywords: ImmutableList<String> = persistentListOf("카페", "편의점", "복사실"),

    // 지도 영역 - 마커
    val showingMarkers: ImmutableList<MapMarker> = persistentListOf(),
    val showingToggleMarkers: ImmutableList<ToggleMarker> = persistentListOf(),

    val buildingMarkers: ImmutableList<BuildingMarker> = persistentListOf(),
    val gateMarkers: ImmutableList<GateMarker> = persistentListOf(),
    val curbMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    val slopeMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    val stairsMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    val specialMarkers: ImmutableList<ToggleMarker> = persistentListOf(),
    val doorMarkers: ImmutableList<DoorMarker> = persistentListOf(),
    val routeDoorMarkers: ImmutableList<DoorMarker> = persistentListOf(),

    // 지도 영역 - 선택된 마커
    val selectedMarker: SelectableMarker? = null,
    val selectedMarkerInfo: MarkerInfo? = null,

    // 지도 영역 - 이미지 다이얼로그
    val isImageDialogVisible: Boolean = false,
    val imageDialogUrls: ImmutableList<String> = persistentListOf(),

    // 지도 영역 - 경로
    val routeResults: ImmutableList<RouteResult> = persistentListOf(),
    val selectedRoute: RouteResult? = null,

    // 바텀 시트 영역
    val buildingSheetInfo: BuildingSheetInfo? = null,
    val convenienceSheetInfo: ConvenienceSheetInfo? = null,
    val searchResults: ImmutableList<SearchResult> = persistentListOf(),

    // 문의 영역
    val inquiryTextField: TextFieldState = TextFieldState(),
    val isInquiryDialogVisible: Boolean = false,

    val homeUiMode: HomeUiMode = HomeUiMode.DEFAULT,
    val searchMode: SearchMode = SearchMode.SEARCH,
    val bottomSheetType: HomeBottomSheetType = HomeBottomSheetType.NONE,
)

/**
 * 모든 마커의 기본 인터페이스
 */
interface MapMarker {
    val id: Long
    val latitude: Double
    val longitude: Double
}

/**
 * 선택 가능한 마커 (건물, 특이사항, 교문)
 */
interface SelectableMarker : MapMarker {
    val isSelected: Boolean
}

/**
 * 건물 마커
 */
data class BuildingMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val name: String,
) : SelectableMarker

/**
 * 토글 마커 (연석, 경사로, 계단, 특이사항)
 */
data class ToggleMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val toggleType: MapToggle,
) : SelectableMarker

/**
 * 교문 마커
 */
data class GateMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val isSelected: Boolean = false,
    val name: String,
) : SelectableMarker

/**
 * 문 마커 (선택 불가)
 */
data class DoorMarker(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    val label: String,
    val isWheelChairAccessible: Boolean = false,
) : MapMarker

/**
 * 마커 정보 (InfoWindow 용)
 */
sealed interface MarkerInfo {
    val description: String
    val imageUrls: ImmutableList<String>
}

data class SpecialMarkerInfo(
    override val description: String,
    override val imageUrls: ImmutableList<String>,
) : MarkerInfo

data class GateMarkerInfo(
    override val description: String,
    override val imageUrls: ImmutableList<String>,
) : MarkerInfo

/**
 * 편의시설 바텀시트 정보
 */
data class ConvenienceSheetInfo(
    val id: Long = 0L,
    val name: String = "",
    val buildingId: Long = 0L,
    val buildingName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

enum class SearchMode {
    NONE,
    SEARCH,
    DEPARTURE,
    DESTINATION,
}

enum class HomeUiMode {
    DEFAULT,
    BARRIER_FREE_INFO,
    FIND_WAY,
}

enum class HomeBottomSheetType {
    NONE,
    SEARCH_RESULT,
    BUILDING_INFO,
    CONVENIENCE_INFO,
}

/**
 * 토글 UI 상태
 */
data class ToggleUiState(
    val toggle: MapToggle = MapToggle.CURB,
) {
    var isSelected by mutableStateOf(false)

    companion object {
        val defaults = MapToggle.entries.map {
            ToggleUiState(
                toggle = it,
            ).apply {
                isSelected = it == MapToggle.SPECIAL_MARK
            }
        }
    }
}

/**
 * 건물 바텀시트 정보
 */
data class BuildingSheetInfo(
    val id: Long = 0L,
    val name: String = "",
    val buildingNumber: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val convenienceList: ImmutableList<Convenience> = persistentListOf(),
    val doorInfoList: ImmutableList<DoorInfo> = persistentListOf(),
)
