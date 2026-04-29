package com.ganaljigi.kubf.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import kotlinx.coroutines.launch

/**
 * 이미지 뷰어 다이얼로그 (전체 화면)
 * - 핀치 줌, 패닝 지원
 * - 여러 이미지를 페이지로 넘길 수 있음
 * @param visible 다이얼로그 표시 여부
 * @param images 이미지 URL 목록
 * @param selectedIndex 초기 선택된 이미지 인덱스
 * @param onDismiss 닫기 콜백
 */
@Composable
fun ImageViewerDialog(
    visible: Boolean,
    images: List<String>,
    selectedIndex: Int = 0,
    onDismiss: () -> Unit,
) {
    if (images.isEmpty()) {
        LaunchedEffect(visible) {
            if (visible) onDismiss()
        }
        return
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            val density = LocalDensity.current
            val screenWidth = with(density) { maxWidth.roundToPx() }
            val screenHeight = with(density) { maxHeight.roundToPx() }
            val pagerState = rememberPagerState(
                initialPage = selectedIndex.coerceIn(0, images.lastIndex),
                pageCount = { images.size },
            )
            val scope = rememberCoroutineScope()
            LaunchedEffect(visible, selectedIndex, images.size) {
                if (visible) {
                    pagerState.scrollToPage(selectedIndex.coerceIn(0, images.lastIndex))
                }
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
            ) { page ->
                images.getOrNull(page)?.let { imageUrl ->
                    TransformableImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clipToBounds(),
                        imageUrl = imageUrl,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight,
                    )
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "닫기",
                    tint = Color.White,
                )
            }
            if (images.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${images.size}",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = KUBFAndroidTheme.typography.medium13,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = {
                                val target = (pagerState.currentPage - 1).coerceAtLeast(0)
                                if (target != pagerState.currentPage) {
                                    scope.launch { pagerState.animateScrollToPage(target) }
                                }
                            },
                            enabled = pagerState.currentPage > 0,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowLeft,
                                contentDescription = "이전 이미지",
                                tint = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.width(40.dp))
                        IconButton(
                            onClick = {
                                val target = (pagerState.currentPage + 1).coerceAtMost(images.lastIndex)
                                if (target != pagerState.currentPage) {
                                    scope.launch { pagerState.animateScrollToPage(target) }
                                }
                            },
                            enabled = pagerState.currentPage < images.lastIndex,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowRight,
                                contentDescription = "다음 이미지",
                                tint = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}
