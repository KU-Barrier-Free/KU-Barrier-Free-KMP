package com.ganaljigi.kubf.feature.building.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_backarrow),
                    contentDescription = "뒤로가기",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = KUBFAndroidTheme.typography.medium15.copy(
                    fontSize = 16.sp,
                ),
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_search_bar_leading),
                    contentDescription = "검색",
                )
            }
        },
    )
}

@Preview
@Composable
private fun BuildingTopAppBarPreview() {
    KUBFAndroidTheme {
        BuildingTopAppBar(
            title = "상허기념도서관",
            onBackClick = {},
            onSearchClick = {},
        )
    }
}
