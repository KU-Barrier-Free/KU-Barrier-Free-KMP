package com.ganaljigi.kubf.feature.helper.mapper

import com.ganaljigi.kubf.feature.helper.response.HelperNoticeDto
import com.ganaljigi.kubf.feature.helper.response.HelperNoticeResponseDto
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperUiState
import com.ganaljigi.kubf.feature.helper.viewmodel.NoticeUi

// 상태? 에 대한~것!
fun HelperNoticeResponseDto.toUiState(): HelperUiState {
    if (!success || result == null) {
        return HelperUiState(
            isLoading = false,
            error = message,
            notices = emptyList(),
        )
    }

    val items = result.map { it.toUi() }

    return HelperUiState(
        // 상태?
        isLoading = false,
        error = null,
        notices = items,
    )
}

// 개별 공지에 대한~것!
fun HelperNoticeDto.toUi(): NoticeUi = NoticeUi(
    title = title,
    date = date,
    url = url,
    rawNumber = number,
    displayNumber = number,
)
