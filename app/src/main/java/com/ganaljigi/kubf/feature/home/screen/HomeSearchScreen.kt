package com.ganaljigi.kubf.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.feature.home.component.search.HomeSearchBar
import com.ganaljigi.kubf.feature.home.component.search.HomeSearchContent
import com.ganaljigi.kubf.feature.home.component.search.HomeSearchTopBar
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiAction
import com.ganaljigi.kubf.feature.home.viewmodel.HomeUiState
import com.ganaljigi.kubf.feature.home.viewmodel.SearchMode

@Composable
fun HomeSearchScreen(
    padding: PaddingValues,
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
) {
    val textFieldState = rememberTextFieldState(uiState.searchText)

    // uiState.searchText가 변경되면 textFieldState에 반영 (인기 검색어 클릭 시)
    LaunchedEffect(uiState.searchText) {
        if (textFieldState.text.toString() != uiState.searchText) {
            textFieldState.edit { replace(0, length, uiState.searchText) }
        }
    }

    val title = when (uiState.searchMode) {
        SearchMode.SEARCH -> "검색"
        SearchMode.DEPARTURE -> "출발지"
        SearchMode.DESTINATION -> "도착지"
        SearchMode.NONE -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding),
    ) {
        HomeSearchTopBar(
            title = title,
            onClick = { onAction(HomeUiAction.OnSearchCloseClick) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        HomeSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textFieldState = textFieldState,
            requestFocus = true,
            onTextChanged = { onAction(HomeUiAction.OnSearchSubmit(it, showSheet = false)) },
            onCleared = { onAction(HomeUiAction.OnSearchInputCleared) },
            onSearchKeyboardEntered = { text ->
                // SEARCH 모드에서만 엔터 허용
                if (uiState.searchMode == SearchMode.SEARCH && text.isNotEmpty()) {
                    onAction(HomeUiAction.OnSearchSubmit(text))
                    onAction(HomeUiAction.OnSearchCloseClick)
                }
            },
        )
        HomeSearchContent(
            searchResults = uiState.searchResults,
            popularKeywords = uiState.popularKeywords,
            onItemClick = { result ->
                when (uiState.searchMode) {
                    SearchMode.SEARCH -> {
                        onAction(HomeUiAction.OnSearchResultClick(result))
                        onAction(HomeUiAction.OnSearchCloseClick)
                    }

                    SearchMode.DEPARTURE -> {
                        onAction(HomeUiAction.OnFromClick(result))
                        onAction(HomeUiAction.OnSearchCloseClick)
                    }

                    SearchMode.DESTINATION -> {
                        onAction(HomeUiAction.OnToClick(result))
                        onAction(HomeUiAction.OnSearchCloseClick)
                    }

                    SearchMode.NONE -> {}
                }
            },
            onPopularKeywordClick = { keyword ->
                onAction(HomeUiAction.OnPopularKeywordClick(keyword))
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchScreenPreview() {
    KUBFAndroidTheme {
        HomeSearchScreen(
            padding = PaddingValues(),
            uiState = HomeUiState(),
            onAction = {},
        )
    }
}
