package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.RoomInfoRepository
import com.ganaljigi.kubf.core.network.api.RoomInfoApi
import com.ganaljigi.kubf.core.network.response.room.RoomInfoResponseDto
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
