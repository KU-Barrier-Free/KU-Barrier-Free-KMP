package com.ganaljigi.kubf.feature.helper.viewmodel

import com.ganaljigi.kubf.core.ui.viewmodel.UiEvent

sealed interface HelperUiEvent : UiEvent {
    data object NavigateBack : HelperUiEvent
    data class OpenUrl(val url: String) : HelperUiEvent
    data class ShowToast(val message: String) : HelperUiEvent
}
