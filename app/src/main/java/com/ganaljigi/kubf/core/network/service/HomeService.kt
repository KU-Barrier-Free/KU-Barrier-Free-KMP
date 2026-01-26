package com.ganaljigi.kubf.core.network.service

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeService {
    @GET("home")
    suspend fun getHomeData(): BaseResponse<HomeResponseDto>

    @GET("home/outside-significants/{outsideSignificantId}")
    suspend fun getSignificantInfo(
        @Path("outsideSignificantId") id: Long
    ): BaseResponse<HomeSignificantResponseDto>

    @GET("places/search")
    suspend fun getHomeSearchResult(
        @Query("keyword") keyword: String,
    ): BaseResponse<HomeSearchResponseDto>

    @GET("home/gates/{gateId}")
    suspend fun getGateInfo(
        @Path("gateId") id: Long
    ): BaseResponse<HomeGateResponseDto>
}