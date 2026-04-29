package com.ganaljigi.kubf.feature.helper.component.topappbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_helper_arrowleft_black
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// 장애학생지원센터 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperTopAppBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
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
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_helper_arrowleft_black),
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@Preview
@Composable
fun PreviewHelperTopAppBar() {
    HelperTopAppBar(
        onBackClick = {},
    )
}
