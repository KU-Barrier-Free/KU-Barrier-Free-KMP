package com.ganaljigi.kubf.feature.room.viewmodel

import com.ganaljigi.kubf.core.ui.viewmodel.UiEvent

sealed interface RoomInfoUiEvent : UiEvent {
    data object NavigateBack : RoomInfoUiEvent
    data class ShowToast(val message: String) : RoomInfoUiEvent
}
