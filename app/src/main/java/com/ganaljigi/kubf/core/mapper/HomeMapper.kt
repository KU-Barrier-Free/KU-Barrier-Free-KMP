package com.ganaljigi.kubf.core.mapper

import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import com.ganaljigi.kubf.core.model.getIconResByName
import com.ganaljigi.kubf.feature.home.model.BuildingMarker
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.SearchResult
import com.ganaljigi.kubf.feature.home.model.ToggleMarker
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiState
import com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun HomeResponseDto.toUiState() = HomeUiState(
    showingBuildingMarkers = this.buildings.toBuildingMarkers(),
    buildingMarkers = this.buildings.toBuildingMarkers(),
    curbMarkers = this.curbs.toToggleMarkers(MapToggle.CURB),
    slopeMarkers = this.ramps.toToggleMarkers(MapToggle.SLOPE),
    stairsMarkers = this.stairs.toToggleMarkers(MapToggle.STAIRS),
    specialMarkers = this.significants.toToggleMarkers(MapToggle.SPECIAL_MARK),
    gateMarkers = this.gates.toGateMarkers(),
    showingToggleMarkers = persistentListOf(
        this.significants.toToggleMarkers(MapToggle.SPECIAL_MARK),
        this.gates.toGateMarkers()
    )
)

fun HomeSignificantResponseDto.toSpecialMarkerInfo() = SpecialMarkerInfo(
    description = this.description,
    imageUrls = this.imageUrls,
)


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
        mapToggle = mapToggle
    )
}.toImmutableList()

fun List<HomeResponseDto.GatePin>.toGateMarkers() = this.map {
    ToggleMarker(
        id = it.id,
        name = it.name,
        latitude = it.latitude,
        longitude = it.longitude,
        mapToggle = MapToggle.GATE
    )
}.toImmutableList()

fun HomeGateResponseDto.toGateMarkerInfo() = SpecialMarkerInfo(
    description = this.description,
    imageUrls = listOf(this.imageUrl),
)

fun HomeSearchResponseDto.toUiState(matchKeyword: String): List<SearchResult> =
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
            icon = getIconResByName(it.purpose)
        )
    }
