package com.ganaljigi.kubf.feature.room.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ganaljigi.kubf.core.navigation.Routes
import com.ganaljigi.kubf.core.ui.viewmodel.BaseViewModel
import com.ganaljigi.kubf.core.mapper.toUiState
import com.ganaljigi.kubf.core.data.repository.RoomInfoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RoomInfoViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RoomInfoRepository,
) : BaseViewModel<RoomInfoUiEvent>() {

    private val route: Routes.RoomInfo = savedStateHandle.toRoute()

    private val _uiState = MutableStateFlow(RoomInfoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRoomInfo()
    }

    fun onRoomInfoUiAction(action: RoomInfoUiAction) {
        when (action) {
            is RoomInfoUiAction.OnBackClick -> onBackClick()
            is RoomInfoUiAction.OnRetry -> loadRoomInfo()
            is RoomInfoUiAction.OnImageClick -> onImageClick(action.imageUrl)
            is RoomInfoUiAction.OnImageDialogClose -> closeImageDialog()
        }
    }

    private fun loadRoomInfo() {
        val buildingId = route.buildingId
        val spaceId = route.spaceId
        val type = route.type
        val buildingNameArg = route.buildingName

        if (buildingId <= 0 || spaceId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            if (type != null) {
                repository.getRoomInfo(buildingId, spaceId, type).fold(
                    onSuccess = { dto ->
                        val mapped = dto.toUiState()
                        _uiState.value = mapped.copy(
                            isLoading = false,
                            buildingName = buildingNameArg ?: mapped.buildingName,
                        )
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "오류") }
                        sendEvent(RoomInfoUiEvent.ShowToast("방 정보를 불러오는데 실패했습니다"))
                    },
                )
            } else {
                runCatching {
                    coroutineScope {
                        val r1 = async { repository.getRoomInfo(buildingId, spaceId, 1) }
                        val r0 = async { repository.getRoomInfo(buildingId, spaceId, 0) }
                        r1.await() to r0.await()
                    }
                }.fold(
                    onSuccess = { (res1, res0) ->
                        val mapped1 = res1.getOrNull()?.toUiState()
                        val mapped0 = res0.getOrNull()?.toUiState()

                        if (mapped1 == null && mapped0 == null) {
                            _uiState.update { it.copy(isLoading = false, error = "데이터 없음") }
                            return@fold
                        }

                        val base = mapped1 ?: mapped0!!
                        val other = mapped0 ?: mapped1

                        val merged = base.copy(
                            roomPicUrls = (base.roomPicUrls + (other?.roomPicUrls ?: emptyList())).distinct(),
                            roomName = base.roomName ?: other?.roomName,
                            department = base.department.ifBlank { other?.department ?: "" },
                            departmentNumber = base.departmentNumber.ifBlank { other?.departmentNumber ?: "" },
                            roomType = base.roomType.ifBlank { other?.roomType ?: "" },
                        )

                        _uiState.value = merged.copy(
                            isLoading = false,
                            buildingName = buildingNameArg ?: merged.buildingName,
                        )
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "오류") }
                        sendEvent(RoomInfoUiEvent.ShowToast("방 정보를 불러오는데 실패했습니다"))
                    },
                )
            }
        }
    }

    private fun onBackClick() {
        viewModelScope.launch { sendEvent(RoomInfoUiEvent.NavigateBack) }
    }

    private fun onImageClick(imageUrl: String) {
        _uiState.update {
            it.copy(
                isImageDialogVisible = true,
                selectedImageUrl = imageUrl,
            )
        }
    }

    private fun closeImageDialog() {
        _uiState.update {
            it.copy(
                isImageDialogVisible = false,
                selectedImageUrl = null,
            )
        }
    }
}
