package com.ganaljigi.kubf.feature.room.repositoryimpl

import com.ganaljigi.kubf.feature.room.response.RoomInfoResponseDto
import com.ganaljigi.kubf.feature.room.service.RoomInfoService
import com.ganaljigi.kubf.feature.room.repository.RoomInfoRepository
import javax.inject.Inject

class RoomInfoRepositoryImpl @Inject constructor(
    private val service: RoomInfoService
) : RoomInfoRepository {
    override suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int
    ): Result<RoomInfoResponseDto> = runCatching {
        service.getRoomInfo(buildingId, spaceId, type)
    }
}