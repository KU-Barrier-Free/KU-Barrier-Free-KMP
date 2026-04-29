package com.ganaljigi.kubf.core.network.response.inquiry

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InquiryRequestDto(
    @SerialName("content")
    val content: String,
)
