package com.ganaljigi.kubf.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

interface UiEvent

abstract class BaseViewModel<Event : UiEvent> : ViewModel() {

    private val _uiEvent = Channel<Event>()
    val uiEvent = _uiEvent.receiveAsFlow()

    protected suspend fun sendEvent(event: Event) {
        _uiEvent.send(event)
    }
}
