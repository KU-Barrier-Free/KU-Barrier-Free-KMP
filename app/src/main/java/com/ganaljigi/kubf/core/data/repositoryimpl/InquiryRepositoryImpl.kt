package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.InquiryRepository
import com.ganaljigi.kubf.core.network.api.InquiryApi
import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.network.response.inquiry.InquiryRequestDto
import com.ganaljigi.kubf.core.network.response.inquiry.InquiryResponseDto
import org.koin.core.annotation.Single

@Single(binds = [InquiryRepository::class])
class InquiryRepositoryImpl(
    private val inquiryApi: InquiryApi,
) : InquiryRepository {
    override suspend fun postInquiry(content: String): Result<InquiryResponseDto> = runCatching {
        inquiryApi.postInquiry(InquiryRequestDto(content)).handleBaseResponse().getOrThrow()
    }
}
