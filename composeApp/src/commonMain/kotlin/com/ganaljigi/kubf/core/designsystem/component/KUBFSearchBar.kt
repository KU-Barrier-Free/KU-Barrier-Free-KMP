package com.ganaljigi.kubf.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_search_bar_leading
import kubfandroid.composeapp.generated.resources.ic_searchbar_close
import com.ganaljigi.kubf.core.designsystem.theme.Gray2
import com.ganaljigi.kubf.core.designsystem.theme.Gray4
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.ui.util.conditionalModifier
import com.ganaljigi.kubf.core.ui.util.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KUBFSearchBar(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onValueCleared: () -> Unit = {},
    onSearchKeyboardClick: () -> Unit = {},
    placeHolderText: String = "",
    interactionSource: MutableInteractionSource,
    isFocused: Boolean = false,
) {
    Row(
        modifier = modifier
            .conditionalModifier(
                condition = isFocused,
                modifierIfTrue = Modifier
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = MainGreen,
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                modifierIfFalse = Modifier
                    .shadow(1.dp, shape = RoundedCornerShape(10.dp), clip = true),
            )
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp)
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search_bar_leading),
            contentDescription = "검색 아이콘",
            tint = if (isFocused) MainGreen else Color.Unspecified,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Gray4), // Cursor color
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchKeyboardClick()
                },
            ),
            textStyle = KUBFAndroidTheme.typography.medium15.copy(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .padding(vertical = 12.dp, horizontal = 7.dp),
                ) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = placeHolderText,
                            style = KUBFAndroidTheme.typography.medium15.copy(
                                color = Gray2,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (value.text.isNotEmpty()) {
            Icon(
                modifier = Modifier.noRippleClickable { onValueCleared() },
                painter = painterResource(Res.drawable.ic_searchbar_close),
                contentDescription = "검색어 비우기",
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun KUBFSearchBar(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    onSearchKeyboardClick: () -> Unit = {},
    onCleared: () -> Unit = {},
    placeHolderText: String = "",
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .conditionalModifier(
                condition = isFocused,
                modifierIfTrue = Modifier
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = MainGreen,
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                modifierIfFalse = Modifier
                    .shadow(1.dp, shape = RoundedCornerShape(10.dp), clip = true),
            )
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp)
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search_bar_leading),
            contentDescription = "검색 아이콘",
            tint = if (isFocused) MainGreen else Color.Unspecified,
        )
        BasicTextField(
            state = state,
            modifier = Modifier
                .weight(1f)
                .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
            textStyle = KUBFAndroidTheme.typography.medium15.copy(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text,
            ),
            onKeyboardAction = { onSearchKeyboardClick() },
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Gray4),
            lineLimits = androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .padding(vertical = 12.dp, horizontal = 7.dp),
                ) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = placeHolderText,
                            style = KUBFAndroidTheme.typography.medium15.copy(
                                color = Gray2,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (state.text.isNotEmpty()) {
            Icon(
                modifier = Modifier.noRippleClickable { onCleared() },
                painter = painterResource(Res.drawable.ic_searchbar_close),
                contentDescription = "검색어 비우기",
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview
@Composable
private fun KUBFSearchBarPreview() {
    var value by remember { mutableStateOf(TextFieldValue()) }
    val interactionSource = remember { MutableInteractionSource() }
    Column {
        KUBFSearchBar(
            modifier = Modifier.padding(20.dp),
            value = value,
            onValueChange = { value = it },
            placeHolderText = "건물, 편의시설 검색",
            interactionSource = interactionSource,
            isFocused = interactionSource.collectIsFocusedAsState().value,
        )
    }
}
