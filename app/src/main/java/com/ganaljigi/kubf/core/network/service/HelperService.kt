package com.ganaljigi.kubf.core.network.service

import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto
import retrofit2.http.GET

interface HelperService {
    // /support-center -> 공지 목록 ㄱㄱ
    @GET("/support-center")
    suspend fun getSupportCenterNotices(): HelperNoticeResponseDto
}
