package com.ganaljigi.kubf.feature.helper.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.ganaljigi.kubf.core.ui.util.ObserveAsEvents
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import com.ganaljigi.kubf.core.ui.util.getPlatformContext
import com.ganaljigi.kubf.core.ui.util.showToast
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.feature.helper.component.information.InfoBox
import com.ganaljigi.kubf.feature.helper.component.information.InformationTitle
import com.ganaljigi.kubf.feature.helper.component.notice.NoticeItem
import com.ganaljigi.kubf.feature.helper.component.notice.NoticeTitle
import com.ganaljigi.kubf.feature.helper.component.shortcut.ShortCutItem
import com.ganaljigi.kubf.feature.helper.component.shortcut.ShortCutTitle
import com.ganaljigi.kubf.feature.helper.component.topappbar.HelperTopAppBar
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperUiAction
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperUiEvent
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperUiState
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperViewModel
import com.ganaljigi.kubf.feature.helper.viewmodel.NoticeUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HelperScreen(
    padding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit,
    navigateToDisableStudentHelper: () -> Unit,
    navigateToSupport: () -> Unit,
    navigateToJobInformation: () -> Unit,
    viewModel: HelperViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val platformContext = getPlatformContext()
    val uriHandler = LocalUriHandler.current

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is HelperUiEvent.NavigateBack -> onBackClick()
            is HelperUiEvent.NavigateToDisableStudentHelper -> navigateToDisableStudentHelper()
            is HelperUiEvent.NavigateToSupport -> navigateToSupport()
            is HelperUiEvent.NavigateToJobInformation -> navigateToJobInformation()
            is HelperUiEvent.OpenUrl -> uriHandler.openUri(event.url)
            is HelperUiEvent.ShowToast -> {
                platformContext.showToast(event.message)
            }
        }
    }

    HelperContent(
        padding = padding,
        uiState = uiState,
        onHelperUiAction = viewModel::onHelperUiAction,
    )
}

@Composable
private fun HelperContent(
    padding: PaddingValues = PaddingValues(),
    uiState: HelperUiState,
    onHelperUiAction: (HelperUiAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            HelperTopAppBar(
                onBackClick = { onHelperUiAction(HelperUiAction.OnBackClick) },
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            NoticeTitle()

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }
                uiState.error != null -> {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = uiState.error ?: "공지사항을 찾을 수 없습니다.")
                    }
                }
                else -> {
                    uiState.notices.forEachIndexed { index, notice ->
                        NoticeItem(
                            title = notice.title,
                            date = notice.date,
                            number = notice.displayNumber,
                            index = index,
                            onClick = { onHelperUiAction(HelperUiAction.OnNoticeClick(notice.url)) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ShortCutTitle()
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ShortCutItem(
                    text = "장애학생 도우미",
                    iconResId = R.drawable.ic_helper_disablestudenthelper,
                    onClick = { onHelperUiAction(HelperUiAction.OnDisableStudentHelperClick) },
                )
                ShortCutItem(
                    text = "지원 업무",
                    iconResId = R.drawable.ic_helper_support,
                    onClick = { onHelperUiAction(HelperUiAction.OnSupportClick) },
                )
                ShortCutItem(
                    text = "채용 정보",
                    iconResId = R.drawable.ic_helper_jobinformation,
                    onClick = { onHelperUiAction(HelperUiAction.OnJobInformationClick) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InformationTitle()
            InfoBox()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun HelperContentPreview() {
    HelperContent(
        uiState = HelperUiState(
            notices = listOf(
                NoticeUi(
                    title = "[KIRD] 포용성장사업_이공계 장애 대학(원)생 경력개발 멘토링 모집 홍보",
                    date = "2025.05.13",
                    url = "",
                    displayNumber = 47,
                ),
                NoticeUi(
                    title = "스텝업탐방캠프 2기 참여자 모집",
                    date = "2025.05.13",
                    url = "",
                    displayNumber = 46,
                ),
                NoticeUi(
                    title = "2025 동행, 국가유산 '빛나는 우리를 만나다' 역사 기행 참여 안내",
                    date = "2025.05.13",
                    url = "",
                    displayNumber = 45,
                ),
            ),
        ),
        onHelperUiAction = {},
    )
}

@Preview
@Composable
private fun HelperContentLoadingPreview() {
    HelperContent(
        uiState = HelperUiState(isLoading = true),
        onHelperUiAction = {},
    )
}

@Preview
@Composable
private fun HelperContentErrorPreview() {
    HelperContent(
        uiState = HelperUiState(error = "공지사항을 불러오는데 실패했습니다."),
        onHelperUiAction = {},
    )
}
