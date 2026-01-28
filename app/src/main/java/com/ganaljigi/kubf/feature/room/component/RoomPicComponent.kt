@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ganaljigi.kubf.feature.room.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomPic(
    roomPicUrls: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: (String) -> Unit = {},
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { roomPicUrls.size })

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val imageUrl = roomPicUrls[page]
            AsyncImage(
                model = imageUrl,
                contentDescription = "강의실 사진",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImageClick(imageUrl) },
            )
        }

        if (roomPicUrls.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .zIndex(2f),
            ) {
                PagerCounter(pagerState = pagerState, total = roomPicUrls.size)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerCounter(
    pagerState: PagerState,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val current by remember(pagerState) { derivedStateOf { pagerState.currentPage } }

    Box(
        modifier = modifier
            .background(Color(0xFF212121).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${current + 1}",
                style = KUBFAndroidTheme.typography.semiBold13.copy(color = Color.White),
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            Text(
                text = "/",
                style = KUBFAndroidTheme.typography.regular13.copy(color = Color.White),
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            Text(
                text = "$total",
                style = KUBFAndroidTheme.typography.regular13.copy(color = Color.White),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomPicPreview() {
    RoomPic(
        roomPicUrls = listOf(
            "https://i.pinimg.com/1200x/10/dc/2e/10dc2ece8b542854d0e276a1114d6190.jpg",
            "https://i.pinimg.com/1200x/d9/5e/3f/d95e3f592893bd3df9e62df37c831638.jpg",
        ),
    )
}
