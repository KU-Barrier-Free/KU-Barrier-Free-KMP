package com.ganaljigi.kubf.feature.helper.repositoryimpl

import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto
import com.ganaljigi.kubf.core.network.service.HelperService
import javax.inject.Inject

class HelperRepositoryImpl @Inject constructor(
    private val service: HelperService
) : HelperRepository {

    override suspend fun fetchNotices(): Result<HelperNoticeResponseDto> = runCatching {
        service.getSupportCenterNotices()
    }
}