package com.ganaljigi.kubf.feature.home.model

data class DoorMarker(
    val id: Long = 0L,
    val label: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isWheelChairAccessible: Boolean = false,
)
