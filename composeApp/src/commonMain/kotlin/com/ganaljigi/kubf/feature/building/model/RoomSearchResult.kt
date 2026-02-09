package com.ganaljigi.kubf.feature.building.model

import org.jetbrains.compose.resources.DrawableResource
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_toggle_curb

data class RoomSearchResult(
    val id: Long = 0L,
    val name: String = "",
    val building: String = "",
    val room: Room? = null,
    val icon: DrawableResource = Res.drawable.ic_toggle_curb,
)
