package com.ganaljigi.kubf.feature.building.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ganaljigi.kubf.core.designsystem.component.KUBFSearchBar
import com.ganaljigi.kubf.core.designsystem.theme.Gray3
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingUiAction
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchPopup(
    viewModel: BuildingViewModel = koinViewModel(),
    onBuildingUiAction: (BuildingUiAction) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    val hasQuery = uiState.query.text.isNotBlank()
    val hasResult = uiState.searchResults.isNotEmpty()

    val expanded = isFocused || hasQuery

    val popupHeight by animateDpAsState(
        targetValue = if (expanded) 480.dp else 302.dp,
    )
    Box(
        modifier = Modifier
            .width(328.dp)
            .height(popupHeight)
            .clip(shape = RoundedCornerShape(6))
            .background(Color.White),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "검색",
                    style = KUBFAndroidTheme.typography.medium15,
                )
            }
            KUBFSearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = uiState.query,
                onValueChange = { value ->
                    onBuildingUiAction(BuildingUiAction.OnQueryChange(value))
                },
                placeHolderText = "강의실명, 호실 검색",
                interactionSource = interactionSource,
                isFocused = isFocused,
                onValueCleared = {
                    onBuildingUiAction(BuildingUiAction.OnQueryClear)
                },
            )

            // 결과 개수: 0이면 숨김
            if (hasQuery && hasResult) {
                Spacer(Modifier.height(24.dp))
                Row(Modifier.padding(horizontal = 16.dp)) {
                    Text("결과 ", style = KUBFAndroidTheme.typography.regular13, color = Gray3)
                    Text(
                        "${uiState.searchResults.size}",
                        style = KUBFAndroidTheme.typography.regular13,
                        color = MainGreen,
                    )
                }
            }

            // 본문 영역 (팝업 내부 스크롤/센터 메시지)
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                when {
                    !hasQuery -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "강의실명, 호실을 검색해보세요.",
                                style = KUBFAndroidTheme.typography.regular14,
                                color = Gray3,
                            )
                        }
                    }

                    hasQuery && !hasResult -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "검색 결과가 없어요.",
                                style = KUBFAndroidTheme.typography.regular14,
                                color = Gray3,
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp),
                        ) {
                            items(
                                items = uiState.searchResults,
                                key = { it.id },
                            ) { item ->
                                val room = item.room ?: return@items
                                RoomComponent(room = room) {
                                    onBuildingUiAction(BuildingUiAction.OnSearchResultClick(item))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
