package com.ganaljigi.kubf.feature.room.repository

import com.ganaljigi.kubf.feature.room.response.RoomInfoResponseDto

interface RoomInfoRepository {
    suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int
    ): Result<RoomInfoResponseDto>
}