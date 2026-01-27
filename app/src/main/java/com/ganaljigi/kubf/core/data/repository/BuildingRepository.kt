package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.feature.building.response.BuildingDto

interface BuildingRepository {
    suspend fun getBuildingInfo(buildingId: Long): Result<BuildingDto>
}
