package com.ganaljigi.kubf.core.network.response.inquiry

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InquiryResponseDto(
    @SerialName("inquiryId")
    val inquiryId: Long,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
)
