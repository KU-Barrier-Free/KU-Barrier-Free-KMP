package com.ganaljigi.kubf.feature.home.model

import org.jetbrains.compose.resources.DrawableResource
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_toggle_curb

data class SearchResult(
    val id: Long = 0L,
    val buildingId: Long = 0L,
    val name: String = "",
    val building: String = "",
    val searchKeyword: String = "",
    val isBuilding: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val icon: DrawableResource = Res.drawable.ic_toggle_curb, // TODO: Response 형식에 맞춰 수정
) {
    fun getBuildingIdByType() = if (isBuilding) this.id else this.buildingId
}
