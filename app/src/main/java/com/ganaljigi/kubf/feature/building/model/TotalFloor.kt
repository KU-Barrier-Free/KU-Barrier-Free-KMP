package com.ganaljigi.kubf.feature.building.model

data class TotalFloor(
    val num : Int=0,
    val floorList: List<FloorInfo> = emptyList()
)