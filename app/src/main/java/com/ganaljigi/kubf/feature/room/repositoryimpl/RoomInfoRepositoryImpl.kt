package com.ganaljigi.kubf.feature.room.repositoryimpl

import com.ganaljigi.kubf.core.network.api.RoomInfoApi
import com.ganaljigi.kubf.feature.room.repository.RoomInfoRepository
import com.ganaljigi.kubf.feature.room.response.RoomInfoResponseDto
import org.koin.core.annotation.Single

@Single(binds = [RoomInfoRepository::class])
class RoomInfoRepositoryImpl(
    private val roomInfoApi: RoomInfoApi,
) : RoomInfoRepository {
    override suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int,
    ): Result<RoomInfoResponseDto> = runCatching {
        roomInfoApi.getRoomInfo(buildingId, spaceId, type)
    }
}
