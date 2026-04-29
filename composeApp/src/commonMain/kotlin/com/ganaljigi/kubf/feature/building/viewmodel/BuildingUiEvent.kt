package com.ganaljigi.kubf.feature.building.viewmodel

import com.ganaljigi.kubf.core.ui.viewmodel.UiEvent
import com.ganaljigi.kubf.feature.building.model.Room

sealed interface BuildingUiEvent : UiEvent {
    data object NavigateBack : BuildingUiEvent
    data class NavigateToRoomInfo(val room: Room, val buildingName: String) : BuildingUiEvent
    data class ShowToast(val message: String) : BuildingUiEvent
    data class ScrollToFloorTab(val index: Int) : BuildingUiEvent
}
