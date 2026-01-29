package com.ganaljigi.kubf.feature.home.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.designsystem.theme.Black
import com.ganaljigi.kubf.core.designsystem.theme.Gray2
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.ui.util.ObserveAsEvents
import com.ganaljigi.kubf.core.ui.util.ensureLocationPermission
import com.ganaljigi.kubf.core.ui.util.getLocationWithPermission
import com.ganaljigi.kubf.core.ui.util.noRippleClickable
import com.ganaljigi.kubf.feature.home.component.BarrierFreeInfoChip
import com.ganaljigi.kubf.feature.home.component.BarrierFreeInfoItem
import com.ganaljigi.kubf.feature.home.component.FindWayButton
import com.ganaljigi.kubf.feature.home.component.HomeToggle
import com.ganaljigi.kubf.feature.home.component.MyLocationButton
import com.ganaljigi.kubf.feature.home.component.NoticeButton
import com.ganaljigi.kubf.feature.home.component.bottomsheet.HomeBuildingInfoSheetContent
import com.ganaljigi.kubf.feature.home.component.bottomsheet.HomeSearchBottomSheet
import com.ganaljigi.kubf.feature.home.component.find.HomeFindTopLocationComponent
import com.ganaljigi.kubf.feature.home.component.find.HomeRouteInfo
import com.ganaljigi.kubf.feature.home.component.map.MapComponent
import com.ganaljigi.kubf.feature.home.component.map.HomeSpecialMarkDialog
import com.ganaljigi.kubf.feature.home.component.search.HomeInquiryDialog
import com.ganaljigi.kubf.feature.home.viewmodel.HomeBottomSheetType
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiAction
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiEvent
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiMode
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiState
import com.ganaljigi.kubf.feature.home.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel

// region Route
@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToHelper: () -> Unit = {},
    navigateToBuildingInfo: (Long) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val context = LocalContext.current

    // Moko Permissions
    val permissionsControllerFactory = rememberPermissionsControllerFactory()
    val permissionsController: PermissionsController = remember(permissionsControllerFactory) {
        permissionsControllerFactory.createPermissionsController()
    }
    BindEffect(permissionsController)

    // 첫 진입 시 권한 요청
    LaunchedEffect(Unit) {
        permissionsController.ensureLocationPermission()
    }

    HomeScreen(
        padding = padding,
        navigateToHelper = navigateToHelper,
        navigateToBuildingInfo = navigateToBuildingInfo,
        viewModel = viewModel,
        permissionsController = permissionsController,
    )
}
// endregion

// region Screen with State
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    padding: PaddingValues,
    navigateToHelper: () -> Unit,
    navigateToBuildingInfo: (Long) -> Unit,
    viewModel: HomeViewModel,
    permissionsController: PermissionsController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
    )
    val bottomSheetState = scaffoldState.bottomSheetState

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5407, 127.0785), 17f)
    }

    // Event 처리
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is HomeUiEvent.MoveCamera -> {
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(event.latitude, event.longitude),
                            event.zoom,
                        ),
                    )
                }
            }

            is HomeUiEvent.NavigateToBuildingInfo -> navigateToBuildingInfo(event.buildingId)
            HomeUiEvent.NavigateToHelper -> navigateToHelper()
            HomeUiEvent.RequestMyLocation -> {
                scope.launch {
                    getLocationWithPermission(context, permissionsController)?.let { location ->
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(location, 17f),
                        )
                    }
                }
            }

            is HomeUiEvent.SetBottomSheetExpanded -> {
                scope.launch {
                    if (event.expanded) bottomSheetState.expand()
                    else bottomSheetState.hide()
                }
            }

            is HomeUiEvent.ShowToast ->
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    // 바텀시트 상태 동기화
    LaunchedEffect(uiState.bottomSheetType) {
        if (uiState.bottomSheetType != HomeBottomSheetType.NONE) {
            bottomSheetState.expand()
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == SheetValue.Hidden) {
            viewModel.onHomeUiAction(HomeUiAction.OnBottomSheetHidden)
        }
    }

    // 뒤로가기 처리
    BackHandler(
        enabled = uiState.homeUiMode != HomeUiMode.DEFAULT || uiState.isSearchScreenShown,
    ) {
        if (uiState.isSearchScreenShown) {
            viewModel.onHomeUiAction(HomeUiAction.OnSearchBackClick)
        } else {
            viewModel.onHomeUiAction(HomeUiAction.OnMapClick)
        }
    }

    HomeScreen(
        padding = padding,
        uiState = uiState,
        scaffoldState = scaffoldState,
        cameraPositionState = cameraPositionState,
        onAction = viewModel::onHomeUiAction,
    )

    // 검색 화면
    if (uiState.isSearchScreenShown) {
        HomeSearchScreen(
            padding = padding,
            uiState = uiState,
            onAction = viewModel::onHomeUiAction,
        )
    }
}
// endregion

// region Stateless Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    padding: PaddingValues,
    uiState: HomeUiState,
    scaffoldState: BottomSheetScaffoldState,
    cameraPositionState: CameraPositionState,
    onAction: (HomeUiAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    BottomSheetScaffold(
        containerColor = Color.White,
        sheetContainerColor = Color.White,
        modifier = Modifier.padding(padding),
        scaffoldState = scaffoldState,
        sheetTonalElevation = 4.dp,
        sheetDragHandle = {},
        sheetContent = {
            when (uiState.bottomSheetType) {
                HomeBottomSheetType.SEARCH_RESULT -> {
                    HomeSearchBottomSheet(
                        searchKeyword = uiState.searchText,
                        searchResults = uiState.searchResults,
                        onInquireClick = { onAction(HomeUiAction.OnInquiryClick) },
                        onFromClick = { onAction(HomeUiAction.OnFromClick(it)) },
                        onToClick = { onAction(HomeUiAction.OnToClick(it)) },
                        onItemClick = { onAction(HomeUiAction.OnSearchResultClick(it)) },
                    )
                }

                HomeBottomSheetType.BUILDING_INFO -> {
                    uiState.buildingSheetInfo?.let { buildingInfo ->
                        HomeBuildingInfoSheetContent(
                            modifier = Modifier.fillMaxWidth(),
                            buildingInfo = buildingInfo,
                            onItemClick = { onAction(HomeUiAction.OnBuildingInfoClick(it)) },
                        )
                    }
                }

                HomeBottomSheetType.CONVENIENCE_INFO -> {
                    HomeSearchBottomSheet(
                        searchKeyword = uiState.searchText,
                        searchResults = uiState.searchResults,
                        onInquireClick = { onAction(HomeUiAction.OnInquiryClick) },
                        onFromClick = { onAction(HomeUiAction.OnFromClick(it)) },
                        onToClick = { onAction(HomeUiAction.OnToClick(it)) },
                        onItemClick = {
                            onAction(HomeUiAction.OnBuildingViewClick(it.getBuildingIdByType()))
                        },
                    )
                }

                HomeBottomSheetType.NONE -> {}
            }
        },
    ) { innerPadding ->

        // 문의 다이얼로그
        if (uiState.isInquiryDialogVisible) {
            HomeInquiryDialog(
                inquiryField = uiState.inquiryTextField,
                onSubmit = { onAction(HomeUiAction.OnInquirySubmit) },
                onDismissRequest = { onAction(HomeUiAction.OnInquiryDialogDismiss) },
            )
        }

        // 이미지 다이얼로그
        if (uiState.isImageDialogVisible) {
            HomeSpecialMarkDialog(
                onDismissRequest = { onAction(HomeUiAction.OnSpecialImageDialogDismiss) },
                imageUrls = uiState.imageDialogUrls,
            )
        }

        // 지도
        val isRouteMode =
            uiState.homeUiMode == HomeUiMode.FIND_WAY && uiState.routeResults.isNotEmpty()
        val findWayMarkers = remember(
            uiState.fromLocation,
            uiState.toLocation,
            uiState.buildingMarkers,
            uiState.routeDoorMarkers,
        ) {
            val locationIds = listOfNotNull(
                uiState.fromLocation?.getBuildingIdByType(),
                uiState.toLocation?.getBuildingIdByType(),
            )
            val buildingMarkers = uiState.buildingMarkers.filter { it.id in locationIds }
            (buildingMarkers + uiState.routeDoorMarkers).toImmutableList()
        }
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            selectedMarker = uiState.selectedMarker,
            selectedMarkerInfo = uiState.selectedMarkerInfo,
            showingMarkers = if (isRouteMode) findWayMarkers else uiState.showingMarkers,
            showingToggleMarkers = if (isRouteMode) persistentListOf() else uiState.showingToggleMarkers,
            routeResult = uiState.selectedRoute,
            onBuildingMarkerClick = { onAction(HomeUiAction.OnBuildingMarkerClick(it)) },
            onGateMarkerClick = { onAction(HomeUiAction.OnGateMarkerClick(it)) },
            onSpecialMarkerClick = { onAction(HomeUiAction.OnSpecialMarkerClick(it)) },
            onSpecialImageClick = { onAction(HomeUiAction.OnSpecialImageClick(it)) },
            onGateImageClick = { onAction(HomeUiAction.OnGateImageClick(it)) },
            onMapClick = { onAction(HomeUiAction.OnMapClick) },
        )

        // UI 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            // 상단 영역
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                // 길찾기 모드
                AnimatedVisibility(
                    visible = uiState.homeUiMode == HomeUiMode.FIND_WAY,
                    enter = slideInVertically(initialOffsetY = { -it / 2 }),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                ) {
                    HomeFindTopLocationComponent(
                        modifier = Modifier.padding(top = 12.dp),
                        fromLocationResult = uiState.fromLocation,
                        toLocationResult = uiState.toLocation,
                        onClose = { onAction(HomeUiAction.OnFindWayCloseClick) },
                        onChange = { onAction(HomeUiAction.OnSwapLocationClick) },
                        onFromLocationClick = { onAction(HomeUiAction.OnFromLocationClick) },
                        onToLocationClick = { onAction(HomeUiAction.OnToLocationClick) },
                    )
                }

                // 기본/배리어프리 모드
                AnimatedVisibility(
                    visible = uiState.homeUiMode == HomeUiMode.DEFAULT ||
                        uiState.homeUiMode == HomeUiMode.BARRIER_FREE_INFO,
                    enter = slideInVertically(initialOffsetY = { -it / 2 }),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .padding(top = 12.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            // 검색창
                            Row(
                                modifier = Modifier
                                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp))
                                    .noRippleClickable { onAction(HomeUiAction.OnSearchBarClick) }
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                                    .height(44.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search_bar_leading),
                                    contentDescription = "검색 아이콘",
                                    tint = Color.Unspecified,
                                )
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = uiState.searchText.ifEmpty { "건물, 편의시설 검색" },
                                    style = KUBFAndroidTheme.typography.medium15.copy(
                                        color = if (uiState.searchText.isEmpty()) Gray2 else Black,
                                    ),
                                )
                            }
                            // 길찾기 버튼
                            FindWayButton(
                                modifier = Modifier.size(44.dp),
                            ) { onAction(HomeUiAction.OnFindWayClick) }
                        }
                        // 토글
                        HomeToggle(
                            modifier = Modifier.fillMaxWidth(),
                            toggleUiStates = uiState.toggleStates,
                            onToggleClick = { toggle ->
                                onAction(HomeUiAction.OnToggleClick(toggle))
                                focusManager.clearFocus()
                            },
                        )
                    }
                }
            }

            // 하단 영역
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                // 배리어프리 설명
                AnimatedVisibility(
                    visible = uiState.homeUiMode == HomeUiMode.BARRIER_FREE_INFO,
                    enter = slideInVertically(initialOffsetY = { it / 2 }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    BarrierFreeInfoItem(
                        onClick = { onAction(HomeUiAction.OnBarrierFreeDescriptionClick) },
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                // 기본 모드 하단 버튼들
                AnimatedVisibility(
                    visible = uiState.homeUiMode == HomeUiMode.DEFAULT,
                    enter = slideInVertically(initialOffsetY = { it / 2 }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        BarrierFreeInfoChip {
                            onAction(HomeUiAction.OnShowBarrierFreeInfoClick)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 40.dp),
                        ) {
                            MyLocationButton { onAction(HomeUiAction.OnMyLocationClick) }
                            NoticeButton { onAction(HomeUiAction.OnHelperClick) }
                        }
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                // 경로 정보
                AnimatedVisibility(
                    visible = uiState.homeUiMode == HomeUiMode.FIND_WAY &&
                        uiState.routeResults.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it / 2 }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    HomeRouteInfo(
                        modifier = Modifier.padding(vertical = 16.dp),
                        selectedRoute = uiState.selectedRoute,
                        routeResults = uiState.routeResults,
                        onRouteSelected = { onAction(HomeUiAction.OnRouteClick(it)) },
                    )
                }
            }
        }
    }
}
// endregion

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    KUBFAndroidTheme {
        HomeScreen(
            padding = PaddingValues(0.dp),
            uiState = HomeUiState(),
            scaffoldState = rememberBottomSheetScaffoldState(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(37.5407, 127.0785), 17f)
            },
            onAction = {},
        )
    }
}
