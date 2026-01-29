package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.inquiry.InquiryRequestDto
import com.ganaljigi.kubf.core.network.response.inquiry.InquiryResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
class InquiryApi(private val client: HttpClient) {
    suspend fun postInquiry(request: InquiryRequestDto): BaseResponse<InquiryResponseDto> =
        client.post("inquiry") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
