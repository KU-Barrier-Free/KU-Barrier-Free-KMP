package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import com.ganaljigi.kubf.core.network.service.HomeService
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService
) : HomeRepository {
    override suspend fun getHomeData(): Result<HomeResponseDto> = runCatching {
        homeService.getHomeData().handleBaseResponse().getOrThrow()
    }

    override suspend fun getSpecialInfo(id: Long): Result<HomeSignificantResponseDto> =
        runCatching {
            homeService.getSignificantInfo(id).handleBaseResponse().getOrThrow()
        }

    override suspend fun getHomeSearchResult(keyword: String): Result<HomeSearchResponseDto> =
        runCatching {
            homeService.getHomeSearchResult(keyword).handleBaseResponse().getOrThrow()
        }

    override suspend fun getGateInfo(id: Long): Result<HomeGateResponseDto> =
        runCatching {
            homeService.getGateInfo(id).handleBaseResponse().getOrThrow()
        }
}