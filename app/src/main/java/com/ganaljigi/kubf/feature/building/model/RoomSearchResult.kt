package com.ganaljigi.kubf.feature.building.model

import androidx.annotation.DrawableRes
import com.ganalijigi.kubf.R

data class RoomSearchResult(
    val id: Long = 0L,
    val name: String = "",
    val building: String = "",
    val room: Room? = null,
    @DrawableRes val icon: Int = R.drawable.ic_toggle_curb,
)
