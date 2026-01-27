package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.BuildingInfoRepository
import com.ganaljigi.kubf.core.network.service.BuildingService
import com.ganaljigi.kubf.feature.building.mapper.toFacilityOrNull
import com.ganaljigi.kubf.feature.building.mapper.toRoomUi
import com.ganaljigi.kubf.feature.building.mapper.toUi
import com.ganaljigi.kubf.feature.building.mapper.toUiPair
import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import com.ganaljigi.kubf.feature.building.model.TotalFloor
import org.koin.core.annotation.Single

@Single(binds = [BuildingInfoRepository::class])
class BuildingInfoRepositoryImpl(
    private val api: BuildingService,
) : BuildingInfoRepository {
    override suspend fun fetchBuilding(id: Long): BuildingInfo {
        val res = api.getBuildingInfo2(id).result
        return BuildingInfo(
            id = res.id,
            name = res.name,
            number = res.number,
            lecture = res.lecture,
            facilities = res.facilityPurposes.mapNotNull { it.toFacilityOrNull() },
            doors = res.doorInfos.map { it.toUi() },
            notes = emptyList(),
            latitude = res.latitude,
            longitude = res.longitude,
        )
    }

    override suspend fun fetchBuildingSpaces(id: Long): Pair<BuildingInfo, TotalFloor> {
        val r = api.getBuildingSpaces(id).result
        return r.toUiPair()
    }

    override suspend fun searchSpaces(id: Long, keyword: String): List<RoomSearchResult> {
        if (keyword.isBlank()) return emptyList()
        val res = api.searchSpaces(id, keyword).result
        return res.spaces.map { s ->
            val room = s.toRoomUi()
            RoomSearchResult(
                id = s.id,
                name = room.name.ifBlank { room.number },
                building = "",
                room = room,
            )
        }
    }
}
