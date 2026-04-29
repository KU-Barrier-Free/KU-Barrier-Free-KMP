package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.network.api.HomeApi
import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto

class HomeRepositoryImpl(
    private val homeApi: HomeApi,
) : HomeRepository {
    override suspend fun getHomeData(): Result<HomeResponseDto> = runCatching {
        homeApi.getHomeData().handleBaseResponse().getOrThrow()
    }

    override suspend fun getSpecialInfo(id: Long): Result<HomeSignificantResponseDto> =
        runCatching {
            homeApi.getSignificantInfo(id).handleBaseResponse().getOrThrow()
        }

    override suspend fun getHomeSearchResult(keyword: String): Result<HomeSearchResponseDto> =
        runCatching {
            homeApi.getHomeSearchResult(keyword).handleBaseResponse().getOrThrow()
        }

    override suspend fun getGateInfo(id: Long): Result<HomeGateResponseDto> =
        runCatching {
            homeApi.getGateInfo(id).handleBaseResponse().getOrThrow()
        }
}
