package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.route.PathResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class RouteApi(private val client: HttpClient) {
    suspend fun getPath(
        srcId: Long,
        srcType: String,
        destId: Long,
        destType: String,
    ): BaseResponse<PathResponseDto> = client.get("path") {
        parameter("srcId", srcId)
        parameter("srcType", srcType)
        parameter("destId", destId)
        parameter("destType", destType)
    }.body()
}
