package com.ganaljigi.kubf.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
)

@Composable
fun KUBFAndroidTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme

    CompositionLocalProvider(
        LocalTypography provides createTypography(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

object KUBFAndroidTheme {
    val typography: KUBFTypography
        @Composable
        get() = LocalTypography.current
}
