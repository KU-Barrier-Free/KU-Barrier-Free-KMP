package com.ganaljigi.kubf.feature.room

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ganaljigi.kubf.core.ui.util.ObserveAsEvents
import com.ganaljigi.kubf.feature.building.component.ImageViewerDialog
import com.ganaljigi.kubf.feature.room.component.DeskAndChairComponent
import com.ganaljigi.kubf.feature.room.component.DoorComponent
import com.ganaljigi.kubf.feature.room.component.RoomInfoDefaultComponent
import com.ganaljigi.kubf.feature.room.component.RoomInfoTopAppBar
import com.ganaljigi.kubf.feature.room.component.RoomPic
import com.ganaljigi.kubf.feature.room.viewmodel.RoomInfoUiAction
import com.ganaljigi.kubf.feature.room.viewmodel.RoomInfoUiEvent
import com.ganaljigi.kubf.feature.room.viewmodel.RoomInfoUiState
import com.ganaljigi.kubf.feature.room.viewmodel.RoomInfoViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomInfoScreen(
    padding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit,
    viewModel: RoomInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is RoomInfoUiEvent.NavigateBack -> onBackClick()
            is RoomInfoUiEvent.ShowToast -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    RoomInfoContent(
        padding = padding,
        uiState = uiState,
        onRoomInfoUiAction = viewModel::onRoomInfoUiAction,
    )
}

@Composable
private fun RoomInfoContent(
    padding: PaddingValues = PaddingValues(),
    uiState: RoomInfoUiState,
    onRoomInfoUiAction: (RoomInfoUiAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val showDeskAndChair = uiState.hasRoomInfo
    val showDoor = uiState.hasRoomInfo

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
    ) {
        if (!uiState.isImageDialogVisible) {
            RoomInfoTopAppBar(
                buildingName = uiState.buildingName,
                onBackClick = { onRoomInfoUiAction(RoomInfoUiAction.OnBackClick) },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            if (uiState.roomPicUrls.isNotEmpty()) {
                RoomPic(
                    roomPicUrls = uiState.roomPicUrls,
                    onImageClick = { imageUrl ->
                        onRoomInfoUiAction(RoomInfoUiAction.OnImageClick(imageUrl))
                    },
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                ) {
                    Text(
                        text = "사진 없음",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }

            RoomInfoDefaultComponent(
                roomNumber = uiState.roomNumber,
                roomName = uiState.roomName,
                lecture = uiState.lecture,
                capacity = uiState.capacity,
                area = uiState.area,
                roomComment = uiState.roomComment,
                floorSpace = uiState.floorSpace,
                roomType = uiState.roomType,
                department = uiState.department,
                departmentNumber = uiState.departmentNumber,
            )

            if (showDeskAndChair) {
                DeskAndChairComponent(
                    allInOne = uiState.allInOne,
                    cinemaSeat = uiState.cinemaSeat,
                    oneSeat = uiState.oneSeat,
                    twoSeat = uiState.twoSeat,
                    multiSeat = uiState.multiSeat,
                    panel = uiState.panel,
                    backOfChair = uiState.backOfChair,
                    wheelChair = uiState.wheelChair,
                    wheelchairTable = uiState.wheelchairTable,
                    computerTable = uiState.computerTable,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (showDoor) {
                DoorComponent(
                    frontDoor = uiState.frontDoor,
                    backDoor = uiState.backDoor,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    ImageViewerDialog(
        visible = uiState.isImageDialogVisible,
        imageUrl = uiState.selectedImageUrl,
        onDismiss = { onRoomInfoUiAction(RoomInfoUiAction.OnImageDialogClose) },
    )
}

@Preview(showBackground = true)
@Composable
private fun RoomInfoScreenPreview() {
    RoomInfoContent(
        uiState = RoomInfoUiState(
            buildingName = "경영관",
            roomPicUrls = listOf(
                "https://i.pinimg.com/1200x/10/dc/2e/10dc2ece8b542854d0e276a1114d6190.jpg",
                "https://i.pinimg.com/1200x/d9/5e/3f/d95e3f592893bd3df9e62df37c831638.jpg",
            ),
            frontDoor = true,
            backDoor = false,
            roomNumber = "102호",
            roomName = "전산실습실",
            lecture = true,
            capacity = 34,
            area = 60.6,
            floorSpace = 18.3,
            roomType = "평탄식",
            department = "정보인프라팀",
            departmentNumber = "010-0000-0000",
            allInOne = false,
            cinemaSeat = false,
            oneSeat = true,
            twoSeat = false,
            multiSeat = false,
            panel = true,
            backOfChair = true,
            wheelChair = true,
            wheelchairTable = false,
            computerTable = false,
        ),
        onRoomInfoUiAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun RoomInfoScreenNoPicPreview() {
    RoomInfoContent(
        uiState = RoomInfoUiState(
            buildingName = "과학관",
            roomPicUrls = emptyList(),
            frontDoor = true,
            backDoor = true,
            roomNumber = "301호",
            roomName = "실험실",
            lecture = false,
            capacity = 20,
            area = 45.0,
            floorSpace = 13.6,
            roomType = "평탄식",
            department = "자연과학대학",
            departmentNumber = "02-450-1234",
            hasRoomInfo = true,
        ),
        onRoomInfoUiAction = {},
    )
}
