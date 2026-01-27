package com.ganaljigi.kubf.feature.helper.repositoryimpl

import com.ganaljigi.kubf.core.network.service.HelperService
import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto
import org.koin.core.annotation.Single

@Single(binds = [HelperRepository::class])
class HelperRepositoryImpl(
    private val service: HelperService,
) : HelperRepository {

    override suspend fun fetchNotices(): Result<HelperNoticeResponseDto> = runCatching {
        service.getSupportCenterNotices()
    }
}
