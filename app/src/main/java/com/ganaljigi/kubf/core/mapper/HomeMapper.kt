package com.ganaljigi.kubf.core.mapper

import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.model.getIconResByName
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.SearchResult
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingMarker
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarker
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiState
import com.ganaljigi.kubf.feature.home.viewmodel.ToggleMarker
import kotlinx.collections.immutable.toImmutableList

fun HomeResponseDto.toHomeUiState(): HomeUiState {
    val buildingMarkers = this.buildings.toBuildingMarkers()
    val specialMarkers = this.significants.toToggleMarkers(MapToggle.SPECIAL_MARK)
    return HomeUiState(
        buildingMarkers = buildingMarkers,
        curbMarkers = this.curbs.toToggleMarkers(MapToggle.CURB),
        slopeMarkers = this.ramps.toToggleMarkers(MapToggle.SLOPE),
        stairsMarkers = this.stairs.toToggleMarkers(MapToggle.STAIRS),
        specialMarkers = specialMarkers,
        gateMarkers = this.gates.toGateMarkers(),
        showingToggleMarkers = specialMarkers,
    )
}

fun List<HomeResponseDto.BuildingPin>.toBuildingMarkers() = this.map {
    BuildingMarker(
        id = it.id,
        name = it.name,
        latitude = it.latitude,
        longitude = it.longitude,
    )
}.toImmutableList()

fun List<HomeResponseDto.HomePin>.toToggleMarkers(
    mapToggle: MapToggle,
) = this.map {
    ToggleMarker(
        id = it.id,
        latitude = it.latitude,
        longitude = it.longitude,
        toggleType = mapToggle,
    )
}.toImmutableList()

fun List<HomeResponseDto.GatePin>.toGateMarkers() = this.map {
    GateMarker(
        id = it.id,
        name = it.name.orEmpty(),
        latitude = it.latitude,
        longitude = it.longitude,
    )
}.toImmutableList()

fun HomeSearchResponseDto.toSearchResults(matchKeyword: String): List<SearchResult> =
    this.buildings.map {
        SearchResult(
            id = it.id,
            buildingId = it.id,
            name = it.name,
            searchKeyword = matchKeyword,
            latitude = it.latitude,
            longitude = it.longitude,
            isBuilding = true,
            icon = R.drawable.ic_building,
        )
    } + this.facilities.map {
        SearchResult(
            id = it.id,
            buildingId = it.buildingId,
            name = it.name,
            building = it.buildingName,
            searchKeyword = matchKeyword,
            latitude = it.latitude,
            longitude = it.longitude,
            isBuilding = false,
            icon = getIconResByName(it.purpose),
        )
    }
