package com.ganaljigi.kubf.core.mapper

import com.ganaljigi.kubf.core.model.DoorInfo
import com.ganaljigi.kubf.core.model.fromLabel
import com.ganaljigi.kubf.core.network.response.building.BuildingDto
import com.ganaljigi.kubf.core.network.response.building.DoorInfoDto
import com.ganaljigi.kubf.core.network.response.building.FloorDto
import com.ganaljigi.kubf.core.network.response.building.SignificantInfosDto
import com.ganaljigi.kubf.core.network.response.building.SpaceSummaryDto
import com.ganaljigi.kubf.core.network.response.building.SpacesDto
import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.Door
import com.ganaljigi.kubf.feature.building.model.Facility
import com.ganaljigi.kubf.feature.building.model.FloorInfo
import com.ganaljigi.kubf.feature.building.model.Note
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.model.TotalFloor
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingSheetInfo
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import kotlinx.collections.immutable.toPersistentList
import kotlin.collections.map
import kotlin.collections.mapNotNull
import kotlin.collections.orEmpty

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

private val facilityMap: Map<String, Facility> = mapOf(
    "카페" to Facility.CAFE,
    "휴게실" to Facility.REST,
    "은행" to Facility.BANK,
    "편의점" to Facility.CONV,
    "복사기" to Facility.PRINT,
    "K-Cube" to Facility.KCUBE,
    "IT-서비스센터" to Facility.SERVICE,
    "주차장" to Facility.PARK,
    "은행" to Facility.BANK,
    "엘리베이터" to Facility.ELEV,
    "우체국" to Facility.POST,
    "장애인화장실" to Facility.TOILET,
    "문화시설" to Facility.CULF,
    "복지매장" to Facility.STORE,
    "식당" to Facility.RES,
)

fun String.toFacilityOrNull(): Facility? = facilityMap[this]

fun DoorInfoDto.toUi() = Door(
    id = id,
    label = label.orEmpty(),
    imageUrl = imageUrl.orEmpty(),
    wheelchair = wheelchair,
    latitude = latitude,
    longitude = longitude,
)

fun SignificantInfosDto.toUi() = Note(
    id = id,
    note = description.orEmpty(),
    imageUrl = imageUrl.orEmpty(),
)

fun SpaceSummaryDto.toRoomUi(): Room {
    val roomImgs = roomImages.filter { it.imageType.equals("ROOM", true) }.map { it.imageUrl }
    val doorImgs = roomImages.filter { it.imageType.equals("DOOR", true) }.map { it.imageUrl }
    return Room(
        id = id,
        number = roomNumber.orEmpty(),
        name = roomName.orEmpty(),
        isLecture = isLecture,
        comment = comment.orEmpty(),
        roomImages = roomImgs,
        doorImages = doorImgs,
    )
}

fun FloorDto.toUi(): FloorInfo = FloorInfo(
    floorLabel = floor,
    imageUrl = drawings.orEmpty(),
    facilities = purposes.orEmpty().mapNotNull { it.toFacilityOrNull() },
    rooms = spaceSummaries.orEmpty().map { it.toRoomUi() },
)

fun SpacesDto.toUiPair(): Pair<BuildingInfo, TotalFloor> {
    val info = BuildingInfo(
        id = id,
        name = name,
        number = number,
        department = department.orEmpty(),
        imageUrl = image.orEmpty(),
        lecture = lecture,
        notes = significantInfos.map {
            Note(
                id = it.id,
                note = it.description,
                imageUrl = it.imageUrl,
            )
        },
        facilities = facilityPurposes.mapNotNull { it.toFacilityOrNull() },
        doors = doorInfos.map {
            Door(
                id = it.id,
                imageUrl = it.imageUrl,
                label = it.label,
                wheelchair = it.wheelchair,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        },
        latitude = latitude,
        longitude = longitude,
    )
    val floors = floorList.map { f ->
        val rooms = f.spaceSummaries.map { s ->
            val roomImgs = s.roomImages.filter { it.imageType.equals("ROOM", true) }.map { it.imageUrl }
            val doorImgs = s.roomImages.filter { it.imageType.equals("DOOR", true) }.map { it.imageUrl }
            Room(
                id = s.id,
                number = s.roomNumber.orEmpty(),
                name = s.roomName.orEmpty(),
                isLecture = s.isLecture,
                comment = s.comment.orEmpty(),
                roomImages = roomImgs,
                doorImages = doorImgs,
            )
        }
        FloorInfo(
            floorLabel = f.floor,
            imageUrl = f.drawings,
            facilities = f.purposes.mapNotNull { it.toFacilityOrNull() },
            rooms = rooms,
        )
    }
    val total = TotalFloor(
        num = floors.size,
        floorList = floors,
    )
    return info to total
}