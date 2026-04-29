package com.ganaljigi.kubf.feature.home.component.map

import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_building
import kubfandroid.composeapp.generated.resources.ic_building_selected
import kubfandroid.composeapp.generated.resources.ic_curb_marker
import kubfandroid.composeapp.generated.resources.ic_gate_pin
import kubfandroid.composeapp.generated.resources.ic_slope_marker
import kubfandroid.composeapp.generated.resources.ic_special_marker
import kubfandroid.composeapp.generated.resources.ic_stairs_marker
import org.jetbrains.compose.resources.DrawableResource

/**
 * iOS Assets에서 직접 UIImage 로드 (네이티브 방식)
 *
 * 사용법: iosApp/iosApp/Assets.xcassets/ 에 이미지 추가 후
 * UIImage.imageNamed("ic_building") 형태로 사용
 */

/**
 * 마커 타입에 따라 적절한 아이콘 리소스 반환
 */
object MarkerIconProvider {
    fun getBuildingIcon(isSelected: Boolean): DrawableResource {
        return if (isSelected) Res.drawable.ic_building_selected else Res.drawable.ic_building
    }

    fun getGateIcon(): DrawableResource {
        return Res.drawable.ic_gate_pin
    }

    fun getCurbIcon(): DrawableResource {
        return Res.drawable.ic_curb_marker
    }

    fun getSlopeIcon(): DrawableResource {
        return Res.drawable.ic_slope_marker
    }

    fun getStairsIcon(): DrawableResource {
        return Res.drawable.ic_stairs_marker
    }

    fun getSpecialIcon(): DrawableResource {
        return Res.drawable.ic_special_marker
    }
}

