package com.ganaljigi.kubf.feature.building.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ganaljigi.kubf.core.data.repository.BuildingInfoRepository
import com.ganaljigi.kubf.core.navigation.Routes
import com.ganaljigi.kubf.core.ui.viewmodel.BaseViewModel
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuildingViewModel(
    savedStateHandle: SavedStateHandle,
    private val buildingInfoRepository: BuildingInfoRepository,
) : BaseViewModel<BuildingUiEvent>() {

    private val buildingId: Long = savedStateHandle.toRoute<Routes.BuildingInfo>().number

    private val _uiState: MutableStateFlow<BuildingUiState> = MutableStateFlow(BuildingUiState())
    val uiState = _uiState.asStateFlow()

    private var sourceRoom: List<Room> = emptyList()

    init {
        loadBuildingInfo()
    }

    fun onBuildingUiAction(action: BuildingUiAction) {
        when (action) {
            is BuildingUiAction.OnQueryChange -> onQueryChange(action.value)
            is BuildingUiAction.OnQueryClear -> clearQuery()
            is BuildingUiAction.OnSearchPopupOpen -> openSearchPopup()
            is BuildingUiAction.OnSearchPopupClose -> closeSearchPopup()
            is BuildingUiAction.OnFloorSelect -> onFloorSelect(action.index)
            is BuildingUiAction.OnRoomClick -> onRoomClick(action.room, action.buildingName)
            is BuildingUiAction.OnSearchResultClick -> onSearchResultClick(action.result)
            is BuildingUiAction.OnNoteImageClick -> onNoteImageClick(action.imageUrl)
            is BuildingUiAction.OnImageDialogClose -> closeImageDialog()
            is BuildingUiAction.OnBackClick -> onBackClick()
        }
    }

    private fun loadBuildingInfo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isSearchPopupVisible = false,
                    query = TextFieldValue(""),
                    searchResults = persistentListOf(),
                    selectedFloorIndex = 0,
                )
            }
            runCatching { buildingInfoRepository.fetchBuildingSpaces(buildingId) }
                .onSuccess { (info, total) ->
                    sourceRoom = total.floorList.flatMap { it.rooms }
                    _uiState.update {
                        it.copy(
                            currentBuildingId = buildingId,
                            currentBuildingName = info.name,
                            buildingInfo = info,
                            totalFloor = total,
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    sendEvent(BuildingUiEvent.ShowToast("건물 정보를 불러오는데 실패했습니다"))
                }
        }
    }

    private fun onQueryChange(value: TextFieldValue) {
        _uiState.update { it.copy(query = value) }
        refreshResults()
    }

    private fun clearQuery() {
        _uiState.update { it.copy(query = TextFieldValue(), searchResults = persistentListOf()) }
    }

    private fun openSearchPopup() {
        _uiState.update { it.copy(isSearchPopupVisible = true) }
    }

    private fun closeSearchPopup() {
        _uiState.update {
            it.copy(
                isSearchPopupVisible = false,
                query = TextFieldValue(""),
                searchResults = persistentListOf(),
            )
        }
    }

    private fun onFloorSelect(index: Int) {
        _uiState.update { it.copy(selectedFloorIndex = index) }
        viewModelScope.launch { sendEvent(BuildingUiEvent.ScrollToFloorTab(index)) }
    }

    private fun onRoomClick(room: Room, buildingName: String) {
        viewModelScope.launch { sendEvent(BuildingUiEvent.NavigateToRoomInfo(room, buildingName)) }
    }

    private fun onSearchResultClick(result: RoomSearchResult) {
        result.room?.let { room ->
            viewModelScope.launch {
                sendEvent(
                    BuildingUiEvent.NavigateToRoomInfo(
                        room = room,
                        buildingName = _uiState.value.currentBuildingName,
                    ),
                )
            }
        }
    }

    private fun onNoteImageClick(imageUrl: String) {
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

    private fun onBackClick() {
        viewModelScope.launch { sendEvent(BuildingUiEvent.NavigateBack) }
    }

    private fun roomNumberKey(num: String?): Int {
        val digits = num?.filter { it.isDigit() }
        return digits?.toIntOrNull() ?: Int.MAX_VALUE
    }

    private fun refreshResults() {
        val q = _uiState.value.query.text.trim()
        if (q.isBlank()) {
            _uiState.update { it.copy(searchResults = persistentListOf()) }
            return
        }
        val roomSearchResults =
            sourceRoom.asSequence()
                .filter { r ->
                    val name = r.name.orEmpty()
                    val num = r.number.orEmpty()
                    name.contains(q, ignoreCase = true) || num.contains(q, ignoreCase = true)
                }
                .sortedWith(
                    compareBy<Room> { roomNumberKey(it.number) }
                        .thenBy { it.name.orEmpty() },
                )
                .map { r ->
                    val display = when {
                        !r.name.isNullOrBlank() -> r.name!!
                        !r.number.isNullOrBlank() -> r.number!!
                        else -> "(이름 없음)"
                    }
                    RoomSearchResult(
                        id = r.id ?: 0L,
                        name = display,
                        building = _uiState.value.currentBuildingName,
                        room = r,
                    )
                }
                .take(50)
                .toPersistentList()
        _uiState.update { it.copy(searchResults = roomSearchResults) }
    }
}
