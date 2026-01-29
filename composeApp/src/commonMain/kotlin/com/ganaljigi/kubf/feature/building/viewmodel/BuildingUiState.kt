package com.ganaljigi.kubf.feature.building.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import com.ganaljigi.kubf.feature.building.model.TotalFloor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class BuildingUiState(
    val currentBuildingId: Long = 0L,
    val currentBuildingName: String = "",
    val buildingInfo: BuildingInfo = BuildingInfo(),
    val totalFloor: TotalFloor = TotalFloor(),

    val selectedFloorIndex: Int = 0,

    val query: TextFieldValue = TextFieldValue(""),
    val searchResults: ImmutableList<RoomSearchResult> = persistentListOf(),
    val isSearchPopupVisible: Boolean = false,

    val isImageDialogVisible: Boolean = false,
    val selectedImageUrl: String? = null,

    val isLoading: Boolean = false,
)
