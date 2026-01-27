package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class HomeApi(private val client: HttpClient) {
    suspend fun getHomeData(): BaseResponse<HomeResponseDto> =
        client.get("home").body()

    suspend fun getSignificantInfo(id: Long): BaseResponse<HomeSignificantResponseDto> =
        client.get("home/outside-significants/$id").body()

    suspend fun getHomeSearchResult(keyword: String): BaseResponse<HomeSearchResponseDto> =
        client.get("places/search") {
            parameter("keyword", keyword)
        }.body()

    suspend fun getGateInfo(id: Long): BaseResponse<HomeGateResponseDto> =
        client.get("home/gates/$id").body()
}
