package com.ganaljigi.kubf.core.network.response.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeGateResponseDto(
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("description")
    val description: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
)
