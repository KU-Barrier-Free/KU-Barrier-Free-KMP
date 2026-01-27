package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.BuildingRepository
import com.ganaljigi.kubf.core.network.api.BuildingApi
import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.feature.building.response.BuildingDto
import org.koin.core.annotation.Single

@Single
class BuildingRepositoryImpl(
    private val buildingApi: BuildingApi,
) : BuildingRepository {
    override suspend fun getBuildingInfo(buildingId: Long): Result<BuildingDto> =
        runCatching {
            buildingApi.getBuildingInfo(buildingId).handleBaseResponse().getOrThrow()
        }
}
