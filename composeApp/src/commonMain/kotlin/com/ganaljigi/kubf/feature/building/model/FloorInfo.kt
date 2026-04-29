package com.ganaljigi.kubf.feature.building.model

data class FloorInfo(
    val floorLabel: String = "",
    val imageUrl: List<String> = listOf(),
    val facilities: List<Facility> = emptyList(),
    val rooms: List<Room> = emptyList(),
)
