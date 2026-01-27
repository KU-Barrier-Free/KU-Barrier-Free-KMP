package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Single

@Single
class HelperApi(private val client: HttpClient) {
    suspend fun getSupportCenterNotices(): HelperNoticeResponseDto =
        client.get("support-center").body()
}
