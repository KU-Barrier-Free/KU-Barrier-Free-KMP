package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.room.RoomInfoResponseDto

interface RoomInfoRepository {
    suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int,
    ): Result<RoomInfoResponseDto>
}
