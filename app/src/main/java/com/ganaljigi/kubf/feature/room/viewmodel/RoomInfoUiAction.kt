package com.ganaljigi.kubf.feature.room.viewmodel

sealed interface RoomInfoUiAction {
    data object OnBackClick : RoomInfoUiAction
    data object OnRetry : RoomInfoUiAction
    data class OnImageClick(val imageUrl: String) : RoomInfoUiAction
    data object OnImageDialogClose : RoomInfoUiAction
}
