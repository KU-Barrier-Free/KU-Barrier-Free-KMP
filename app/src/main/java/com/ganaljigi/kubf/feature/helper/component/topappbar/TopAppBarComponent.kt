package com.ganaljigi.kubf.feature.helper.component.topappbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganalijigi.kubf.R

// 장애학생지원센터 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperTopAppBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "장애학생지원센터",
                    style = KUBFAndroidTheme.typography.semiBold20.copy(
                        fontSize = 20.sp,
                    ),
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_helper_arrowleft_black),
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            // .height(84.dp)
            // .height(64.dp)
            // .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.statusBars),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHelperTopAppBar() {
    HelperTopAppBar(
        onBackClick = {},
    )
}
