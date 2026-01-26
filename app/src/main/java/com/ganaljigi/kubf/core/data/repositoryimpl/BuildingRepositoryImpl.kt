package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.data.repository.BuildingRepository
import com.ganaljigi.kubf.core.network.response.building.BuildingSummaryResponseDto
import com.ganaljigi.kubf.core.network.service.BuildingService
import javax.inject.Inject

class BuildingRepositoryImpl @Inject constructor(
    private val buildingService: BuildingService,
) : BuildingRepository {
    override suspend fun getBuildingInfo(buildingId: Long): Result<BuildingSummaryResponseDto> =
        runCatching {
            buildingService.getBuildingInfo(buildingId).handleBaseResponse().getOrThrow()
        }
}
