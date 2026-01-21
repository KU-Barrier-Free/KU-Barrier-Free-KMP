package com.ganaljigi.kubf.ui.home.component.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganaljigi.kubf.ui.theme.Gray2
import com.ganaljigi.kubf.ui.theme.KUBFAndroidTheme

@Composable
fun MapGateInfo(
    description: String,
    modifier: Modifier = Modifier,
    painters: List<Painter>,
    scale: Float = 1f,
) {
    val cornerRadius by remember(scale) { mutableStateOf(20.dp * scale) }
    val imageSize by remember(scale) { mutableStateOf(160.dp * scale) }
    val fontSize by remember(scale) { mutableFloatStateOf((21 * scale).coerceAtLeast(10f)) }
    val lineHeight by remember(scale) { mutableFloatStateOf((33 * scale).coerceAtLeast(14f)) }

    Column(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                color = Gray2.copy(alpha = 0.5f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(12.dp * scale)
            .widthIn(max = 320.dp * scale),
        verticalArrangement = Arrangement.spacedBy(16.dp * scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp * scale)
        ) {
            painters.forEach {
                Image(
                    painter = it,
                    contentDescription = description,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(10.dp * scale)),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Text(
            text = description,
            style = KUBFAndroidTheme.typography.semiBold14.copy(
                fontSize = fontSize.sp,
                lineHeight = lineHeight.sp,
            ),
            textAlign = TextAlign.Center
        )
    }
}
