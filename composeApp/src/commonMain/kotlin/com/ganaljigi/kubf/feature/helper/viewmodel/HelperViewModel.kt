package com.ganaljigi.kubf.feature.helper.viewmodel

import androidx.lifecycle.viewModelScope
import com.ganaljigi.kubf.core.ui.viewmodel.BaseViewModel
import com.ganaljigi.kubf.feature.helper.mapper.toUiState
import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HelperViewModel(
    private val repository: HelperRepository,
) : BaseViewModel<HelperUiEvent>() {

    private val _uiState = MutableStateFlow(HelperUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotices()
    }

    fun onHelperUiAction(action: HelperUiAction) {
        when (action) {
            is HelperUiAction.OnBackClick -> onBackClick()
            is HelperUiAction.OnRetry -> loadNotices()
            is HelperUiAction.OnNoticeClick -> onNoticeClick(action.url)
            is HelperUiAction.OnDisableStudentHelperClick -> onDisableStudentHelperClick()
            is HelperUiAction.OnSupportClick -> onSupportClick()
            is HelperUiAction.OnJobInformationClick -> onJobInformationClick()
        }
    }

    private fun loadNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.fetchNotices().fold(
                onSuccess = { dto ->
                    val mapped = dto.toUiState()

                    val top3 = mapped.notices
                        .sortedByDescending { it.date.toLocalDateOrMin() }
                        .take(3)

                    _uiState.value = mapped.copy(
                        isLoading = false,
                        notices = top3,
                    )
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "") }
                    sendEvent(HelperUiEvent.ShowToast("공지사항을 불러오는데 실패했습니다"))
                },
            )
        }
    }

    private fun onBackClick() {
        viewModelScope.launch { sendEvent(HelperUiEvent.NavigateBack) }
    }

    private fun onNoticeClick(url: String) {
        viewModelScope.launch { sendEvent(HelperUiEvent.OpenUrl(url)) }
    }

    private fun onDisableStudentHelperClick() {
        viewModelScope.launch { sendEvent(HelperUiEvent.NavigateToDisableStudentHelper) }
    }

    private fun onSupportClick() {
        viewModelScope.launch { sendEvent(HelperUiEvent.NavigateToSupport) }
    }

    private fun onJobInformationClick() {
        viewModelScope.launch { sendEvent(HelperUiEvent.NavigateToJobInformation) }
    }

    // "yyyy.MM.dd" 형식 파싱
    private fun String.toLocalDateOrMin(): LocalDate = runCatching {
        val parts = this.split(".")
        if (parts.size == 3) {
            LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            LocalDate(1970, 1, 1)
        }
    }.getOrElse { LocalDate(1970, 1, 1) }
}
