package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.room.RoomInfoResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class RoomInfoApi(private val client: HttpClient) {
    suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int,
    ): RoomInfoResponseDto = client.get("buildings/$buildingId/spaces/$spaceId") {
        parameter("type", type)
    }.body()
}
