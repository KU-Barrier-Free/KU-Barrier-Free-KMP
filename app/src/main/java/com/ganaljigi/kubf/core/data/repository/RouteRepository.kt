package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.route.PathResponseDto

interface RouteRepository {
    suspend fun getPath(
        srcId: Long,
        srcType: String,
        destId: Long,
        destType: String
    ): Result<PathResponseDto>
}