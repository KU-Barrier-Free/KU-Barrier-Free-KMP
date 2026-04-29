package com.ganaljigi.kubf.feature.home.viewmodel

import com.ganaljigi.kubf.core.ui.viewmodel.UiEvent

sealed interface HomeUiEvent : UiEvent {
    // 토스트 메시지 표시
    data class ShowToast(val message: String) : HomeUiEvent

    // 네비게이션
    data object NavigateToHelper : HomeUiEvent
    data class NavigateToBuildingInfo(val buildingId: Long) : HomeUiEvent

    // 위치 권한 요청
    data object RequestMyLocation : HomeUiEvent

    // 바텀시트
    data class SetBottomSheetExpanded(val expanded: Boolean) : HomeUiEvent

    // 카메라 이동
    data class MoveCamera(val latitude: Double, val longitude: Double, val zoom: Float = 17f) :
        HomeUiEvent
}
