package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.building.BuildingSummaryResponseDto

interface BuildingRepository {
    suspend fun getBuildingInfo(buildingId: Long): Result<BuildingSummaryResponseDto>
}