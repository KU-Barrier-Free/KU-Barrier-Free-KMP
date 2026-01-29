package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.building.BuildingDto

interface BuildingRepository {
    suspend fun getBuildingInfo(buildingId: Long): Result<BuildingDto>
}
