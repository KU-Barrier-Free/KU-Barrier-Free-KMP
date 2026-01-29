package com.ganaljigi.kubf.feature.building.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.ui.util.ObserveAsEvents
import com.ganaljigi.kubf.core.ui.util.getPlatformContext
import com.ganaljigi.kubf.core.ui.util.showToast
import com.ganaljigi.kubf.feature.building.component.BuildingHeaderComponent
import com.ganaljigi.kubf.feature.building.component.BuildingTopAppBar
import com.ganaljigi.kubf.feature.building.component.FloorComponent
import com.ganaljigi.kubf.feature.building.component.FloorTabRow
import com.ganaljigi.kubf.feature.building.component.ImageViewerDialog
import com.ganaljigi.kubf.feature.building.component.SearchPopup
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingUiAction
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingUiEvent
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BuildingInfoScreen(
    padding: PaddingValues = PaddingValues(),
    onBack: () -> Unit,
    onRoomClick: (Room, String) -> Unit,
    viewModel: BuildingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val platformContext = getPlatformContext()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is BuildingUiEvent.NavigateBack -> onBack()
            is BuildingUiEvent.NavigateToRoomInfo -> onRoomClick(event.room, event.buildingName)
            is BuildingUiEvent.ShowToast -> {
                platformContext.showToast(event.message)
            }

            is BuildingUiEvent.ScrollToFloorTab -> {
                scope.launch { listState.animateScrollToItem(1) }
            }
        }
    }

    val floors = uiState.totalFloor.floorList
    val safeIndex = uiState.selectedFloorIndex.coerceIn(0, (floors.size - 1).coerceAtLeast(0))
    val current = floors.getOrNull(safeIndex)
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
    ) {
        if (!uiState.isImageDialogVisible) {
            BuildingTopAppBar(
                title = uiState.buildingInfo.name,
                onBackClick = { viewModel.onBuildingUiAction(BuildingUiAction.OnBackClick) },
                onSearchClick = { viewModel.onBuildingUiAction(BuildingUiAction.OnSearchPopupOpen) },
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            item {
                BuildingHeaderComponent(
                    buildingInfo = uiState.buildingInfo,
                    onImageClick = { imageUrl ->
                        viewModel.onBuildingUiAction(BuildingUiAction.OnNoteImageClick(imageUrl))
                    },
                )
            }
            stickyHeader {
                FloorTabRow(
                    floors = floors,
                    selectedIndex = safeIndex,
                    onFloorSelect = { idx ->
                        viewModel.onBuildingUiAction(BuildingUiAction.OnFloorSelect(idx))
                    },
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
                current?.let { floor ->
                    FloorComponent(
                        current = floor,
                        onRoomClick = { room ->
                            viewModel.onBuildingUiAction(
                                BuildingUiAction.OnRoomClick(room, uiState.buildingInfo.name),
                            )
                        },
                        onImageClick = { imageUrl ->
                            viewModel.onBuildingUiAction(
                                BuildingUiAction.OnNoteImageClick(imageUrl),
                            )
                        },
                        buildingName = uiState.buildingInfo.name,
                    )
                }
            }
        }
    }
    if (uiState.isSearchPopupVisible) {
        Dialog(
            onDismissRequest = {
                viewModel.onBuildingUiAction(BuildingUiAction.OnSearchPopupClose)
            },
        ) {
            SearchPopup(
                onBuildingUiAction = viewModel::onBuildingUiAction,
            )
        }
    }

    ImageViewerDialog(
        visible = uiState.isImageDialogVisible,
        imageUrl = uiState.selectedImageUrl,
        onDismiss = { viewModel.onBuildingUiAction(BuildingUiAction.OnImageDialogClose) },
    )
}

@Preview
@Composable
private fun PreviewBuilding() {
    KUBFAndroidTheme {
        BuildingInfoScreen(
            onBack = {},
            onRoomClick = { _, _ -> },
        )
    }
}
