package com.ganaljigi.kubf.feature.home.model

import org.jetbrains.compose.resources.DrawableResource
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_toggle_curb
import kubfandroid.composeapp.generated.resources.ic_toggle_slope
import kubfandroid.composeapp.generated.resources.ic_toggle_stairs
import kubfandroid.composeapp.generated.resources.ic_toggle_special
import kubfandroid.composeapp.generated.resources.ic_curb_marker
import kubfandroid.composeapp.generated.resources.ic_slope_marker
import kubfandroid.composeapp.generated.resources.ic_stairs_marker
import kubfandroid.composeapp.generated.resources.ic_special_marker

enum class MapToggle(
    val label: String,
    val toggleIconRes: DrawableResource,
    val markerIconRes: DrawableResource,
) {
    // 연석, 경사로, 계단, 특이사항
    CURB("연석", Res.drawable.ic_toggle_curb, Res.drawable.ic_curb_marker),
    SLOPE("경사로", Res.drawable.ic_toggle_slope, Res.drawable.ic_slope_marker),
    STAIRS("계단", Res.drawable.ic_toggle_stairs, Res.drawable.ic_stairs_marker),
    SPECIAL_MARK("특이사항", Res.drawable.ic_toggle_special, Res.drawable.ic_special_marker),
}
