package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.RouteRepository
import com.ganaljigi.kubf.core.network.api.RouteApi
import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.network.response.route.PathResponseDto
import org.koin.core.annotation.Single

@Single
class RouteRepositoryImpl(
    private val routeApi: RouteApi,
) : RouteRepository {
    override suspend fun getPath(
        srcId: Long,
        srcType: String,
        destId: Long,
        destType: String,
    ): Result<PathResponseDto> = runCatching {
        routeApi.getPath(srcId, srcType, destId, destType).handleBaseResponse().getOrThrow()
    }
}
