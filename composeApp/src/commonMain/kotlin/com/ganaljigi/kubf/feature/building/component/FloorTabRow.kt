package com.ganaljigi.kubf.feature.building.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ganaljigi.kubf.core.designsystem.theme.Gray4
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.feature.building.model.FloorInfo

@Composable
fun FloorTabRow(
    floors: List<FloorInfo>,
    selectedIndex: Int,
    onFloorSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (floors.isEmpty()) return

    val safeIndex = selectedIndex.coerceIn(0, floors.size - 1)

    if (floors.size < 7) {
        TabRow(
            selectedTabIndex = safeIndex,
            modifier = modifier,
            indicator = { position ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(position[safeIndex])
                        .height(2.dp),
                    color = MainGreen,
                )
            },
            containerColor = Color.White,
        ) {
            floors.forEachIndexed { idx, floorInfo ->
                FloorTab(
                    floorLabel = floorInfo.floorLabel,
                    selected = idx == safeIndex,
                    onClick = { onFloorSelect(idx) },
                )
            }
        }
    } else {
        ScrollableTabRow(
            selectedTabIndex = safeIndex,
            modifier = modifier.fillMaxWidth(),
            indicator = { position ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(position[safeIndex])
                        .height(2.dp),
                    color = MainGreen,
                )
            },
            edgePadding = 0.dp,
            containerColor = Color.White,
        ) {
            floors.forEachIndexed { idx, floorInfo ->
                FloorTab(
                    floorLabel = floorInfo.floorLabel,
                    selected = idx == safeIndex,
                    onClick = { onFloorSelect(idx) },
                )
            }
        }
    }
}

@Composable
private fun FloorTab(
    floorLabel: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                text = "${floorLabel}층",
                textAlign = TextAlign.Center,
                style = if (selected) {
                    KUBFAndroidTheme.typography.regular14
                } else {
                    KUBFAndroidTheme.typography.medium14
                },
                color = if (selected) MainGreen else Gray4,
            )
        },
    )
}

@Preview
@Composable
private fun FloorTabRowPreview() {
    KUBFAndroidTheme {
        FloorTabRow(
            floors = listOf(
                FloorInfo(floorLabel = "B1"),
                FloorInfo(floorLabel = "1"),
                FloorInfo(floorLabel = "2"),
                FloorInfo(floorLabel = "3"),
            ),
            selectedIndex = 1,
            onFloorSelect = {},
        )
    }
}

@Preview
@Composable
private fun FloorTabRowScrollablePreview() {
    KUBFAndroidTheme {
        FloorTabRow(
            floors = listOf(
                FloorInfo(floorLabel = "B2"),
                FloorInfo(floorLabel = "B1"),
                FloorInfo(floorLabel = "1"),
                FloorInfo(floorLabel = "2"),
                FloorInfo(floorLabel = "3"),
                FloorInfo(floorLabel = "4"),
                FloorInfo(floorLabel = "5"),
                FloorInfo(floorLabel = "6"),
            ),
            selectedIndex = 3,
            onFloorSelect = {},
        )
    }
}
