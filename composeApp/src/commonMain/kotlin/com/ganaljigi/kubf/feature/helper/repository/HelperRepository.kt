package com.ganaljigi.kubf.feature.helper.repository

import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto

interface HelperRepository {
    suspend fun fetchNotices(): Result<HelperNoticeResponseDto>
}
