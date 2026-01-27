package com.ganaljigi.kubf.core.mapper

import com.ganaljigi.kubf.core.model.DoorInfo
import com.ganaljigi.kubf.core.model.fromLabel
import com.ganaljigi.kubf.feature.building.response.BuildingDto
import com.ganaljigi.kubf.feature.building.response.DoorInfoDto
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingSheetInfo
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import kotlinx.collections.immutable.toPersistentList

fun BuildingDto.toHomeBuildingSheetInfo() = BuildingSheetInfo(
    id = id,
    name = name,
    buildingNumber = number,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    convenienceList = facilityPurposes.mapNotNull { fromLabel(it) }.toPersistentList(),
    doorInfoList = doorInfos.map { it.toDoorInfo() }.toPersistentList(),
)

fun DoorInfoDto.toDoorInfo() = DoorInfo(
    id = this.id,
    imageUrl = this.imageUrl.firstOrNull() ?: "",
    label = label,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    isWheelchairAccessible = wheelchair,
    description = "",
    imageUrls = this.imageUrl,
)

fun DoorInfoDto.toDoorMarker() = DoorMarker(
    id = this.id,
    label = this.label,
    latitude = this.latitude ?: 0.0,
    longitude = this.longitude ?: 0.0,
    isWheelChairAccessible = this.wheelchair,
)

fun BuildingDto.toDoorMarkers() = doorInfos.map { it.toDoorMarker() }
