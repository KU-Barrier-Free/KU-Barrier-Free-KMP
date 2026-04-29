package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.building.BuildingDto
import com.ganaljigi.kubf.core.network.response.building.SearchResponseDto
import com.ganaljigi.kubf.core.network.response.building.SpacesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BuildingApi(private val client: HttpClient) {
    suspend fun getBuildingInfo(buildingId: Long): BaseResponse<BuildingDto> =
        client.get("buildings/$buildingId").body()

    suspend fun getBuildingSpaces(id: Long): BaseResponse<SpacesDto> =
        client.get("buildings/$id/spaces").body()

    suspend fun searchSpaces(id: Long, keyword: String): BaseResponse<SearchResponseDto> =
        client.get("buildings/$id/spaces/search") {
            parameter("keyword", keyword)
        }.body()
}
