package com.ganaljigi.kubf.feature.building.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.Door
import com.ganaljigi.kubf.feature.building.model.Facility
import com.ganaljigi.kubf.feature.building.model.FloorInfo
import com.ganaljigi.kubf.feature.building.model.Note
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import com.ganaljigi.kubf.feature.building.model.TotalFloor
import kotlinx.collections.immutable.persistentListOf

data class BuildingUIState(
    val door: Door = Door(),
    val floorInfo: FloorInfo = FloorInfo(),
    val buildingInfo: BuildingInfo = BuildingInfo(),
    val note: Note? = null,
    val room: Room = Room(),
    val totalFloor: TotalFloor = TotalFloor(),
    val facility: Facility = Facility.CAFE,

    val currentBuildingId: Long = 0L,
    val currentBuildingName: String = "",
    val query: TextFieldValue = TextFieldValue(""),
    val result: List<RoomSearchResult> = persistentListOf(),
    val isSearching: Boolean = false,
)
