package com.ganaljigi.kubf.feature.helper.repositoryimpl

import com.ganaljigi.kubf.core.network.api.HelperApi
import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto

class HelperRepositoryImpl(
    private val helperApi: HelperApi,
) : HelperRepository {

    override suspend fun fetchNotices(): Result<HelperNoticeResponseDto> = runCatching {
        helperApi.getSupportCenterNotices()
    }
}
