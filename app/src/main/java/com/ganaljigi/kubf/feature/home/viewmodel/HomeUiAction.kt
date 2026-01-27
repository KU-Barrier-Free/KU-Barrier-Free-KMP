package com.ganaljigi.kubf.feature.home.viewmodel

import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.model.SearchResult

// HomeUiAction
sealed interface HomeUiAction {
    // 상단 영역
    data object OnSearchBarClick : HomeUiAction
    data object OnSearchCloseClick : HomeUiAction
    data class OnToggleClick(val toggle: MapToggle) : HomeUiAction
    data object OnFindWayClick : HomeUiAction
    data object OnFromLocationClick : HomeUiAction
    data object OnToLocationClick : HomeUiAction
    data object OnFindWayCloseClick : HomeUiAction
    data object OnSwapLocationClick : HomeUiAction
    data class OnRouteClick(val route: RouteResult) : HomeUiAction

    // 검색 영역
    data object OnSearchBackClick : HomeUiAction
    data object OnSearchInputCleared : HomeUiAction
    data class OnSearchSubmit(val keyword: String, val showSheet: Boolean = true) : HomeUiAction
    data class OnPopularKeywordClick(val keyword: String) : HomeUiAction
    data class OnSearchResultClick(val result: SearchResult) : HomeUiAction

    // 지도 영역
    data object OnMapClick : HomeUiAction
    data class OnGateMarkerClick(val marker: GateMarker) : HomeUiAction
    data class OnGateImageClick(val imageUrls: List<String>) : HomeUiAction
    data class OnBuildingMarkerClick(val marker: BuildingMarker) : HomeUiAction
    data class OnSpecialMarkerClick(val marker: ToggleMarker) : HomeUiAction
    data class OnSpecialImageClick(val imageUrls: List<String>) : HomeUiAction
    data object OnSpecialImageDialogDismiss : HomeUiAction
    data object OnMyLocationClick : HomeUiAction
    data object OnHelperClick : HomeUiAction
    data object OnShowBarrierFreeInfoClick : HomeUiAction
    data object OnBarrierFreeDescriptionClick : HomeUiAction

    // 바텀 시트 영역
    data object OnBottomSheetHidden : HomeUiAction

    // 바텀 시트 영역 - 건물
    data class OnBuildingInfoClick(val buildingId: Long) : HomeUiAction
    data class OnDoorClick(val imageUrls: List<String>) : HomeUiAction

    // 바텀 시트 영역 - 편의시설
    data class OnFromClick(val result: SearchResult) : HomeUiAction
    data class OnToClick(val result: SearchResult) : HomeUiAction
    data class OnBuildingViewClick(val buildingId: Long) : HomeUiAction

    // 문의
    data object OnInquiryClick : HomeUiAction
    data object OnInquirySubmit : HomeUiAction
    data object OnInquiryDialogDismiss : HomeUiAction
}
