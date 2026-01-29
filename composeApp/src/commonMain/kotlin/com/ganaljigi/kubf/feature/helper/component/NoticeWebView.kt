package com.ganaljigi.kubf.feature.helper.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NoticeWebView(
    url: String,
    modifier: Modifier = Modifier,
)
