package com.ganaljigi.kubf.feature.building.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.feature.building.model.FloorInfo
import com.ganaljigi.kubf.feature.building.model.Room

@Composable
fun FloorComponent(
    current: FloorInfo,
    onRoomClick: (Room) -> Unit,
    onImageClick: (String) -> Unit = {},
    buildingName: String = "",
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            if (current.imageUrl.isNotEmpty()) {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { current.imageUrl.size },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp)),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        val url = current.imageUrl[page]
                        AsyncImage(
                            model = url,
                            contentDescription = "$buildingName ${current.floorLabel}층 도면",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onImageClick(url) },
                        )
                    }
                    if (current.imageUrl.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1}/${current.imageUrl.size}",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = KUBFAndroidTheme.typography.medium13,
                            )
                        }
                    }
                }
            }
            if (current.facilities.isNotEmpty()) {
                FacilityComponent(current.facilities)
                Spacer(Modifier.height(20.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                current.rooms.forEach { room ->
                    RoomComponent(
                        room = room,
                        onClick = { onRoomClick(room) },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun FloorCompPreview() {
    KUBFAndroidTheme {
        FloorComponent(
            current = FloorInfo(),
            onRoomClick = { },
        )
    }
}
