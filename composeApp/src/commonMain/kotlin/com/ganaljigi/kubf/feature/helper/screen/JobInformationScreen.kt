package com.ganaljigi.kubf.feature.helper.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ganaljigi.kubf.feature.helper.component.NoticeWebView
import com.ganaljigi.kubf.feature.helper.component.WebViewTopAppBar

@Composable
fun JobInformationScreen(
    padding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
    ) {
        WebViewTopAppBar(
            textTitle = "채용 정보",
            onBackClick = onBackClick,
        )
        NoticeWebView(
            url = "https://www.konkuk.ac.kr/csd/15240/subview.do",
            modifier = Modifier.fillMaxSize(),
        )
    }
}
