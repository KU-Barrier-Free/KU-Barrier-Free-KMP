package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.data.repository.RouteRepository
import com.ganaljigi.kubf.core.network.response.route.PathResponseDto
import com.ganaljigi.kubf.core.network.service.RouteService
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeService: RouteService,
) : RouteRepository {
    override suspend fun getPath(
        srcId: Long,
        srcType: String,
        destId: Long,
        destType: String,
    ): Result<PathResponseDto> = runCatching {
        routeService.getPath(srcId, srcType, destId, destType).handleBaseResponse().getOrThrow()
    }
}
