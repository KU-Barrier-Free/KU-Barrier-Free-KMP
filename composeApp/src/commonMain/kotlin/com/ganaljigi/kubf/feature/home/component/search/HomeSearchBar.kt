package com.ganaljigi.kubf.feature.home.component.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ganaljigi.kubf.core.designsystem.component.KUBFSearchBar
import com.ganaljigi.kubf.core.designsystem.theme.Gray1
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.core.ui.util.noRippleClickable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    focusRequester: FocusRequester = remember { FocusRequester() },
    requestFocus: Boolean = false,
    onTextChanged: (String) -> Unit = {},
    onCleared: () -> Unit = {},
    onSearchKeyboardEntered: (String) -> Unit = {},
) {
    // 텍스트 변경 감지
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .debounce(300)
            .collectLatest { text ->
                onTextChanged(text)
            }
    }

    // 자동 포커스
    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
            ),
    ) {
        KUBFSearchBar(
            modifier = Modifier.fillMaxWidth(),
            state = textFieldState,
            onSearchKeyboardClick = {
                onSearchKeyboardEntered(textFieldState.text.toString())
            },
            onCleared = {
                textFieldState.edit { replace(0, length, "") }
                onCleared()
            },
            placeHolderText = "건물, 편의시설 검색",
            focusRequester = focusRequester,
        )
    }
}

@Composable
fun ToggleChip(
    modifier: Modifier = Modifier,
    searchKeyword: String,
    onChipClick: (String) -> Unit = {},
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Gray1,
                shape = RoundedCornerShape(20.dp),
            )
            .noRippleClickable { onChipClick(searchKeyword) }
            .padding(horizontal = 8.dp, vertical = 7.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = searchKeyword,
            style = KUBFAndroidTheme.typography.regular12.copy(
                color = MainGreen,
            ),
        )
    }
}

@Preview
@Composable
private fun HomeSearchBarPreview() {
    HomeSearchBar(
        onTextChanged = {},
    )
}
