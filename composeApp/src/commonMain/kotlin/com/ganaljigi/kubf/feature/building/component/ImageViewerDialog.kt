package com.ganaljigi.kubf.feature.building.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.designsystem.component.TransformableImage
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme

@Composable
fun ImageViewerDialog(
    visible: Boolean,
    imageUrl: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() }

    AnimatedVisibility(
        visible = visible && imageUrl != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .systemBarsPadding()
                .fillMaxSize(),
        ) {
            imageUrl?.let { url ->
                TransformableImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds(),
                    imageUrl = url,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                )
            }
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .align(Alignment.TopEnd)
                    .clickable { onDismiss() }
                    .padding(16.dp),
                painter = painterResource(R.drawable.ic_searchbar_close),
                contentDescription = "닫기",
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview
@Composable
private fun ImageViewerDialogPreview() {
    KUBFAndroidTheme {
        ImageViewerDialog(
            visible = true,
            imageUrl = "https://example.com/image.jpg",
            onDismiss = {},
        )
    }
}
