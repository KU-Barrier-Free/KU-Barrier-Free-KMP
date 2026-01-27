package com.ganaljigi.kubf.feature.room.repositoryimpl

import com.ganaljigi.kubf.feature.room.repository.RoomInfoRepository
import com.ganaljigi.kubf.feature.room.response.RoomInfoResponseDto
import com.ganaljigi.kubf.feature.room.service.RoomInfoService
import org.koin.core.annotation.Single

@Single(binds = [RoomInfoRepository::class])
class RoomInfoRepositoryImpl(
    private val service: RoomInfoService,
) : RoomInfoRepository {
    override suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int,
    ): Result<RoomInfoResponseDto> = runCatching {
        service.getRoomInfo(buildingId, spaceId, type)
    }
}
