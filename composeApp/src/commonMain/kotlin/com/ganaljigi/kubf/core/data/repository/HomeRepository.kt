package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto

interface HomeRepository {
    suspend fun getHomeData(): Result<HomeResponseDto>

    suspend fun getSpecialInfo(id: Long): Result<HomeSignificantResponseDto>

    suspend fun getHomeSearchResult(keyword: String): Result<HomeSearchResponseDto>

    suspend fun getGateInfo(id: Long): Result<HomeGateResponseDto>
}
