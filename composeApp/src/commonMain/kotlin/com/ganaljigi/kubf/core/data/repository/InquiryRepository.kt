package com.ganaljigi.kubf.core.data.repository

import com.ganaljigi.kubf.core.network.response.inquiry.InquiryResponseDto

interface InquiryRepository {
    suspend fun postInquiry(content: String): Result<InquiryResponseDto>
}
