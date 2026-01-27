package com.ganaljigi.kubf.feature.home.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.ganaljigi.kubf.core.data.repository.BuildingRepository
import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.data.repository.RouteRepository
import com.ganaljigi.kubf.core.mapper.toDoorMarkers
import com.ganaljigi.kubf.core.mapper.toHomeBuildingSheetInfo
import com.ganaljigi.kubf.core.mapper.toHomeUiState
import com.ganaljigi.kubf.core.mapper.toRouteResults
import com.ganaljigi.kubf.core.mapper.toSearchResults
import com.ganaljigi.kubf.core.ui.viewmodel.BaseViewModel
import com.ganaljigi.kubf.feature.home.model.MapToggle
import com.ganaljigi.kubf.feature.home.model.RouteResult
import com.ganaljigi.kubf.feature.home.model.SearchResult
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val buildingRepository: BuildingRepository,
    private val routeRepository: RouteRepository,
) : BaseViewModel<HomeUiEvent>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchInitData()
    }

    fun onHomeUiAction(action: HomeUiAction) {
        when (action) {
            // 상단 영역 - 검색 화면 표시/숨김
            HomeUiAction.OnSearchBarClick -> setSearchScreen(true, SearchMode.SEARCH)
            HomeUiAction.OnSearchCloseClick -> setSearchScreen(false, SearchMode.NONE)
            HomeUiAction.OnFromLocationClick -> setSearchScreen(true, SearchMode.DEPARTURE)
            HomeUiAction.OnToLocationClick -> setSearchScreen(true, SearchMode.DESTINATION)
            is HomeUiAction.OnToggleClick -> updateToggleState(action.toggle)
            HomeUiAction.OnFindWayClick -> enterFindWayMode()
            HomeUiAction.OnFindWayCloseClick -> exitFindWayMode()
            HomeUiAction.OnSwapLocationClick -> swapAndFetchRoute()
            is HomeUiAction.OnRouteClick -> selectRoute(action.route)
            // 검색 영역
            HomeUiAction.OnSearchBackClick -> resetSearchState()
            HomeUiAction.OnSearchInputCleared -> clearSearchInput()
            is HomeUiAction.OnSearchSubmit -> searchKeyword(action.keyword, action.showSheet)
            is HomeUiAction.OnPopularKeywordClick -> searchKeyword(action.keyword, showSheet = false)
            is HomeUiAction.OnSearchResultClick -> handleSearchResultClick(action.result)
            // 지도 영역
            HomeUiAction.OnMapClick -> resetToDefaultState()
            HomeUiAction.OnBottomSheetHidden -> resetToDefaultState()
            is HomeUiAction.OnGateMarkerClick -> fetchGateMarkerInfo(action.marker)
            is HomeUiAction.OnSpecialMarkerClick -> fetchSpecialMarkerInfo(action.marker)
            is HomeUiAction.OnBuildingMarkerClick -> selectBuildingMarker(action.marker)
            // 이미지 다이얼로그
            is HomeUiAction.OnGateImageClick -> showImageDialog(action.imageUrls)
            is HomeUiAction.OnSpecialImageClick -> showImageDialog(action.imageUrls)
            is HomeUiAction.OnDoorClick -> showImageDialog(action.imageUrls)
            HomeUiAction.OnSpecialImageDialogDismiss -> hideImageDialog()
            // 네비게이션
            HomeUiAction.OnMyLocationClick -> sendEventAsync(HomeUiEvent.RequestMyLocation)
            HomeUiAction.OnHelperClick -> sendEventAsync(HomeUiEvent.NavigateToHelper)
            is HomeUiAction.OnBuildingInfoClick -> navigateToBuildingInfo(action.buildingId)
            is HomeUiAction.OnBuildingViewClick -> navigateToBuildingInfo(action.buildingId)
            // 배리어프리 모드
            HomeUiAction.OnShowBarrierFreeInfoClick -> setHomeUiMode(HomeUiMode.BARRIER_FREE_INFO)
            HomeUiAction.OnBarrierFreeDescriptionClick -> setHomeUiMode(HomeUiMode.DEFAULT)
            // 출발/도착 설정
            is HomeUiAction.OnFromClick -> setFromLocation(action.result)
            is HomeUiAction.OnToClick -> setToLocation(action.result)
            // 문의
            HomeUiAction.OnInquiryClick -> showInquiryDialog()
            HomeUiAction.OnInquirySubmit -> submitInquiry()
            HomeUiAction.OnInquiryDialogDismiss -> hideInquiryDialog()
        }
    }

    private fun setSearchScreen(shown: Boolean, mode: SearchMode) {
        _uiState.update {
            it.copy(
                isSearchScreenShown = shown,
                searchMode = mode,
            )
        }
    }

    private fun setHomeUiMode(mode: HomeUiMode) {
        _uiState.update { it.copy(homeUiMode = mode) }
    }

    private fun getDefaultShowingMarkers() =
        (_uiState.value.buildingMarkers + _uiState.value.gateMarkers).toImmutableList()

    private fun enterFindWayMode() {
        _uiState.update {
            it.copy(
                selectedMarker = null,
                buildingSheetInfo = null,
                selectedMarkerInfo = null,
                bottomSheetType = HomeBottomSheetType.NONE,
                homeUiMode = HomeUiMode.FIND_WAY,
            )
        }
    }

    private fun exitFindWayMode() {
        _uiState.update {
            it.copy(
                fromLocation = null,
                toLocation = null,
                searchResults = persistentListOf(),
                routeResults = persistentListOf(),
                routeDoorMarkers = persistentListOf(),
                selectedRoute = null,
                homeUiMode = HomeUiMode.DEFAULT,
            )
        }
    }

    private fun swapAndFetchRoute() {
        _uiState.update {
            it.copy(
                fromLocation = it.toLocation,
                toLocation = it.fromLocation,
            )
        }
        fetchRoute()
    }

    private fun selectRoute(route: RouteResult) {
        _uiState.update { it.copy(selectedRoute = route) }
    }

    private fun resetSearchState() {
        _uiState.update {
            it.copy(
                searchText = "",
                fromLocation = null,
                toLocation = null,
                searchMode = SearchMode.NONE,
                searchResults = persistentListOf(),
                routeResults = persistentListOf(),
                selectedRoute = null,
                homeUiMode = HomeUiMode.DEFAULT,
            )
        }
    }

    private fun clearSearchInput() {
        _uiState.update {
            it.copy(
                searchText = "",
                searchResults = persistentListOf(),
            )
        }
    }

    private fun selectBuildingMarker(marker: BuildingMarker) {
        _uiState.update {
            it.copy(
                selectedMarker = marker,
                selectedMarkerInfo = null,
            )
        }
        fetchBuildingInfo(marker.id)
    }

    private fun showImageDialog(imageUrls: List<String>) {
        _uiState.update {
            it.copy(
                isImageDialogVisible = true,
                imageDialogUrls = imageUrls.toImmutableList(),
                bottomSheetType = HomeBottomSheetType.NONE,
            )
        }
    }

    private fun hideImageDialog() {
        _uiState.update {
            it.copy(
                isImageDialogVisible = false,
                imageDialogUrls = persistentListOf(),
                selectedMarker = null,
                selectedMarkerInfo = null,
                bottomSheetType = HomeBottomSheetType.NONE,
            )
        }
    }

    private fun showInquiryDialog() {
        _uiState.update { it.copy(isInquiryDialogVisible = true) }
    }

    private fun submitInquiry() {
        sendEventAsync(HomeUiEvent.ShowToast("문의가 등록되었습니다."))
        hideInquiryDialog()
    }

    private fun hideInquiryDialog() {
        _uiState.update {
            it.copy(
                isInquiryDialogVisible = false,
                inquiryTextField = TextFieldState(),
            )
        }
    }

    private fun resetToDefaultState() {
        _uiState.update {
            it.copy(
                selectedMarker = null,
                buildingSheetInfo = null,
                selectedMarkerInfo = null,
                searchText = "",
                fromLocation = null,
                toLocation = null,
                searchMode = SearchMode.NONE,
                searchResults = persistentListOf(),
                routeResults = persistentListOf(),
                selectedRoute = null,
                homeUiMode = HomeUiMode.DEFAULT,
                bottomSheetType = HomeBottomSheetType.NONE,
                doorMarkers = persistentListOf(),
                showingMarkers = getDefaultShowingMarkers(),
            )
        }
        sendEventAsync(HomeUiEvent.SetBottomSheetExpanded(false))
    }

    private fun updateToggleState(toggle: MapToggle) {
        _uiState.update {
            it.toggleStates.forEach { toggleUiState ->
                if (toggleUiState.toggle == toggle) {
                    toggleUiState.isSelected = !toggleUiState.isSelected
                }
            }

            val newShowingToggleMarkers = it.toggleStates
                .filter { state -> state.isSelected }
                .flatMap { state ->
                    when (state.toggle) {
                        MapToggle.CURB -> _uiState.value.curbMarkers
                        MapToggle.SLOPE -> _uiState.value.slopeMarkers
                        MapToggle.STAIRS -> _uiState.value.stairsMarkers
                        MapToggle.SPECIAL_MARK -> _uiState.value.specialMarkers
                    }
                }.toImmutableList()

            it.copy(
                showingToggleMarkers = newShowingToggleMarkers,
                selectedMarker = null,
                selectedMarkerInfo = null,
            )
        }
    }

    private fun sendEventAsync(event: HomeUiEvent) {
        viewModelScope.launch { sendEvent(event) }
    }

    private fun navigateToBuildingInfo(buildingId: Long) {
        sendEventAsync(HomeUiEvent.NavigateToBuildingInfo(buildingId))
    }

    private fun searchKeyword(keyword: String, showSheet: Boolean) {
        if (keyword.isEmpty()) return

        viewModelScope.launch {
            homeRepository.getHomeSearchResult(keyword).fold(
                onSuccess = { response ->
                    val results = response.toSearchResults(keyword).toImmutableList()
                    _uiState.update {
                        it.copy(
                            searchText = keyword,
                            searchResults = results,
                            bottomSheetType = if (showSheet) {
                                HomeBottomSheetType.SEARCH_RESULT
                            } else {
                                it.bottomSheetType
                            },
                        )
                    }
                    if (showSheet) {
                        sendEvent(HomeUiEvent.SetBottomSheetExpanded(true))
                    }
                },
                onFailure = { error ->
                    Napier.e("getSearchResults error", error)
                    sendEvent(HomeUiEvent.ShowToast("검색 결과를 불러오는데 실패했습니다."))
                },
            )
        }
    }

    private fun handleSearchResultClick(result: SearchResult) {
        sendEventAsync(HomeUiEvent.MoveCamera(result.latitude, result.longitude, 17f))

        if (result.isBuilding) {
            _uiState.update { it.copy(searchText = result.name) }
            fetchBuildingInfo(result.id)
        } else {
            _uiState.update {
                it.copy(
                    searchText = result.name,
                    selectedMarker = null,
                    buildingSheetInfo = null,
                    selectedMarkerInfo = null,
                    searchResults = persistentListOf(result),
                    bottomSheetType = HomeBottomSheetType.CONVENIENCE_INFO,
                )
            }
            sendEventAsync(HomeUiEvent.SetBottomSheetExpanded(true))
        }
    }

    private fun setFromLocation(result: SearchResult) {
        _uiState.update {
            it.copy(
                fromLocation = result,
                homeUiMode = HomeUiMode.FIND_WAY,
            )
        }
        if (_uiState.value.toLocation != null) fetchRoute()
    }

    private fun setToLocation(result: SearchResult) {
        _uiState.update {
            it.copy(
                toLocation = result,
                homeUiMode = HomeUiMode.FIND_WAY,
            )
        }
        if (_uiState.value.fromLocation != null) fetchRoute()
    }

    private fun fetchInitData() {
        viewModelScope.launch {
            homeRepository.getHomeData().fold(
                onSuccess = { response ->
                    val newState = response.toHomeUiState()
                    val defaultShowingMarkers =
                        (newState.buildingMarkers + newState.gateMarkers).toImmutableList()
                    _uiState.update {
                        it.copy(
                            buildingMarkers = newState.buildingMarkers,
                            curbMarkers = newState.curbMarkers,
                            slopeMarkers = newState.slopeMarkers,
                            stairsMarkers = newState.stairsMarkers,
                            specialMarkers = newState.specialMarkers,
                            gateMarkers = newState.gateMarkers,
                            showingMarkers = defaultShowingMarkers,
                            showingToggleMarkers = newState.showingToggleMarkers,
                        )
                    }
                },
                onFailure = { error ->
                    Napier.e("fetchInitData error", error)
                    sendEvent(HomeUiEvent.ShowToast("데이터를 불러오는데 실패했습니다."))
                },
            )
        }
    }

    private fun fetchBuildingInfo(buildingId: Long) {
        viewModelScope.launch {
            buildingRepository.getBuildingInfo(buildingId).fold(
                onSuccess = { response ->
                    val doorMarkers = response.toDoorMarkers()
                    val selectedBuilding =
                        _uiState.value.buildingMarkers.find { it.id == buildingId }
                    val newShowingMarkers =
                        listOfNotNull(selectedBuilding) + doorMarkers + _uiState.value.gateMarkers
                    _uiState.update {
                        it.copy(
                            buildingSheetInfo = response.toHomeBuildingSheetInfo(),
                            doorMarkers = doorMarkers.toImmutableList(),
                            showingMarkers = newShowingMarkers.toImmutableList(),
                            bottomSheetType = HomeBottomSheetType.BUILDING_INFO,
                        )
                    }
                    sendEvent(HomeUiEvent.SetBottomSheetExpanded(true))
                },
                onFailure = { error ->
                    Napier.e("getBuildingInfo error", error)
                },
            )
        }
    }

    private fun fetchGateMarkerInfo(marker: GateMarker) {
        viewModelScope.launch {
            homeRepository.getGateInfo(marker.id)
                .onSuccess { response ->
                    val markerInfo = GateMarkerInfo(
                        description = response.description,
                        imageUrls = persistentListOf(response.imageUrl),
                    )
                    _uiState.update {
                        it.copy(
                            bottomSheetType = HomeBottomSheetType.NONE,
                            selectedMarker = marker,
                            selectedMarkerInfo = markerInfo,
                            doorMarkers = persistentListOf(),
                            showingMarkers = getDefaultShowingMarkers(),
                        )
                    }
                }
                .onFailure { error ->
                    Napier.e("getGateMarkerInfo error", error)
                }
        }
    }

    private fun fetchSpecialMarkerInfo(marker: ToggleMarker) {
        viewModelScope.launch {
            homeRepository.getSpecialInfo(marker.id).fold(
                onSuccess = { response ->
                    val markerInfo = SpecialMarkerInfo(
                        description = response.description,
                        imageUrls = response.imageUrls.toImmutableList(),
                    )
                    _uiState.update {
                        it.copy(
                            bottomSheetType = HomeBottomSheetType.NONE,
                            selectedMarker = marker,
                            selectedMarkerInfo = markerInfo,
                            doorMarkers = persistentListOf(),
                            showingMarkers = getDefaultShowingMarkers(),
                        )
                    }
                },
                onFailure = { error ->
                    Napier.e("getSpecialMarkerInfo error", error)
                },
            )
        }
    }

    private fun fetchRoute() {
        val fromLocation = _uiState.value.fromLocation ?: return
        val toLocation = _uiState.value.toLocation ?: return

        // 출발지와 도착지가 동일한 경우
        if (fromLocation.id == toLocation.id) {
            sendEventAsync(HomeUiEvent.ShowToast("출발지와 도착지가 동일합니다."))
            return
        }

        viewModelScope.launch {
            routeRepository.getPath(
                srcId = fromLocation.id,
                srcType = if (fromLocation.isBuilding) "BUILDING" else "FACILITY",
                destId = toLocation.id,
                destType = if (toLocation.isBuilding) "BUILDING" else "FACILITY",
            ).fold(
                onSuccess = { response ->
                    val routeResults = response.toRouteResults()
                    if (routeResults.isNotEmpty()) {
                        // 출발지/도착지 문 마커 조회
                        val doorMarkers = mutableListOf<DoorMarker>()
                        if (fromLocation.isBuilding) {
                            buildingRepository.getBuildingInfo(fromLocation.id)
                                .onSuccess { doorMarkers.addAll(it.toDoorMarkers()) }
                        }
                        if (toLocation.isBuilding) {
                            buildingRepository.getBuildingInfo(toLocation.id)
                                .onSuccess { doorMarkers.addAll(it.toDoorMarkers()) }
                        }

                        _uiState.update {
                            it.copy(
                                homeUiMode = HomeUiMode.FIND_WAY,
                                routeResults = routeResults.toImmutableList(),
                                selectedRoute = routeResults.first(),
                                routeDoorMarkers = doorMarkers.toImmutableList(),
                                isSearchScreenShown = false,
                                searchMode = SearchMode.NONE,
                            )
                        }
                        // 카메라를 출발지와 도착지 중간으로 이동
                        val centerLat = (fromLocation.latitude + toLocation.latitude) / 2
                        val centerLng = (fromLocation.longitude + toLocation.longitude) / 2
                        sendEvent(HomeUiEvent.MoveCamera(centerLat, centerLng, 16f))
                    } else {
                        sendEvent(HomeUiEvent.ShowToast("경로를 찾을 수 없습니다."))
                    }
                },
                onFailure = { error ->
                    Napier.e("fetchRoute error", error)
                    sendEvent(HomeUiEvent.ShowToast("경로를 불러오는데 실패했습니다."))
                },
            )
        }
    }
}
