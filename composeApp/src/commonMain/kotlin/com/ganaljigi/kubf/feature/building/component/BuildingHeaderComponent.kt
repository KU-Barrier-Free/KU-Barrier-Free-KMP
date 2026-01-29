package com.ganaljigi.kubf.feature.building.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ganaljigi.kubf.core.designsystem.theme.Gray3
import com.ganaljigi.kubf.core.designsystem.theme.Gray4
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.feature.building.model.BuildingInfo
import com.ganaljigi.kubf.feature.building.model.Door
import com.ganaljigi.kubf.feature.building.model.Facility
import com.ganaljigi.kubf.feature.building.model.Note

@Composable
fun BuildingHeaderComponent(
    buildingInfo: BuildingInfo,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // 건물 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    buildingInfo.imageUrl
                        .takeIf { it.isNotBlank() }
                        ?.let { url -> onImageClick(url) }
                },
        ) {
            AsyncImage(
                model = buildingInfo.imageUrl,
                contentDescription = "${buildingInfo.name} 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .background(color = Color.LightGray)
                    .fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(16.dp))

        // 건물 이름, 번호
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = buildingInfo.name,
                style = KUBFAndroidTheme.typography.bold18,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "건물번호: ${
                    if (buildingInfo.number == 0) "없음" else buildingInfo.number.toString()
                }",
                style = KUBFAndroidTheme.typography.regular14,
                color = Gray3,
            )
        }
        Spacer(Modifier.height(24.dp))

        // 소속 부서
        Text(
            text = "소속 부서",
            style = KUBFAndroidTheme.typography.semiBold16,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = buildingInfo.department,
            style = KUBFAndroidTheme.typography.regular14,
            color = Gray4,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(20.dp))

        // 주요시설
        Text(
            text = "주요시설",
            style = KUBFAndroidTheme.typography.semiBold16,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        FacilityComponent(
            facilities = buildingInfo.facilities,
        )
        Spacer(Modifier.height(20.dp))

        // 출입문
        Text(
            text = "출입문",
            style = KUBFAndroidTheme.typography.semiBold16,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        DoorComponent(
            doors = buildingInfo.doors,
            onImageClick = onImageClick,
        )
        Spacer(Modifier.height(20.dp))

        // 특이사항
        if (buildingInfo.notes.isNotEmpty()) {
            Text(
                text = "특이사항",
                style = KUBFAndroidTheme.typography.semiBold16,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            buildingInfo.notes.forEachIndexed { idx, note ->
                NoteComponent(
                    note = note,
                    onImageClick = onImageClick,
                )
                if (idx < buildingInfo.notes.lastIndex) {
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.height(16.dp))

        // 층별 정보 제목
        Text(
            text = "층별 정보",
            style = KUBFAndroidTheme.typography.semiBold16,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Preview
@Composable
private fun BuildingHeaderComponentPreview() {
    KUBFAndroidTheme {
        BuildingHeaderComponent(
            buildingInfo = BuildingInfo(
                id = 1L,
                name = "상허기념도서관",
                number = 12,
                department = "도서관, 학술정보원",
                imageUrl = "",
                notes = listOf(
                    Note(id = 1L, note = "1층 장애인 화장실 있음"),
                    Note(id = 2L, note = "엘리베이터 이용 가능"),
                ),
                facilities = listOf(Facility.CAFE, Facility.REST, Facility.ELEV),
                doors = listOf(
                    Door(id = 1L, label = "정문", wheelchair = true),
                    Door(id = 2L, label = "후문", wheelchair = false),
                ),
            ),
            onImageClick = {},
        )
    }
}

@Preview
@Composable
private fun BuildingHeaderComponentNoNotesPreview() {
    KUBFAndroidTheme {
        BuildingHeaderComponent(
            buildingInfo = BuildingInfo(
                id = 1L,
                name = "공학관",
                number = 0,
                department = "공과대학",
                imageUrl = "",
                notes = emptyList(),
                facilities = listOf(Facility.ELEV, Facility.TOILET),
                doors = listOf(
                    Door(id = 1L, label = "정문", wheelchair = true),
                ),
            ),
            onImageClick = {},
        )
    }
}
