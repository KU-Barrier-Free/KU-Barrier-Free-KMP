package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import com.ganaljigi.kubf.feature.building.model.TotalFloor

interface BuildingInfoRepository {
    suspend fun fetchBuilding(id: Long): BuildingInfo
    suspend fun fetchBuildingSpaces(id: Long): Pair<BuildingInfo, TotalFloor>
    suspend fun searchSpaces(id: Long, keyword: String): List<RoomSearchResult>
}
