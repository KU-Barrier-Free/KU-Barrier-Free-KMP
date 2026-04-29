package com.ganaljigi.kubf.feature.building.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult

sealed interface BuildingUiAction {
    // 검색
    data class OnQueryChange(val value: TextFieldValue) : BuildingUiAction
    data object OnQueryClear : BuildingUiAction
    data object OnSearchPopupOpen : BuildingUiAction
    data object OnSearchPopupClose : BuildingUiAction

    // 층 선택
    data class OnFloorSelect(val index: Int) : BuildingUiAction

    // 방 클릭
    data class OnRoomClick(val room: Room, val buildingName: String) : BuildingUiAction
    data class OnSearchResultClick(val result: RoomSearchResult) : BuildingUiAction

    // 이미지
    data class OnNoteImageClick(val imageUrl: String) : BuildingUiAction
    data object OnImageDialogClose : BuildingUiAction

    // 네비게이션
    data object OnBackClick : BuildingUiAction
}
