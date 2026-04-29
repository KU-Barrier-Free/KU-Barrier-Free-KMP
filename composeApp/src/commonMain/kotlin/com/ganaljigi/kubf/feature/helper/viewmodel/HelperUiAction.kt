package com.ganaljigi.kubf.feature.helper.viewmodel

sealed interface HelperUiAction {
    data object OnBackClick : HelperUiAction
    data object OnRetry : HelperUiAction
    data class OnNoticeClick(val url: String) : HelperUiAction
    data object OnDisableStudentHelperClick : HelperUiAction
    data object OnSupportClick : HelperUiAction
    data object OnJobInformationClick : HelperUiAction
}
