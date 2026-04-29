package com.ganaljigi.kubf.feature.home.component.map.marker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganaljigi.kubf.core.designsystem.theme.Gray4
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.feature.home.viewmodel.DoorMarker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState

@Composable
fun DoorMarkerComposable(
    doorMarker: DoorMarker,
    scale: Float = 1f,
) {
    val fontSize = (14 * scale).coerceAtLeast(10f)

    MarkerComposable(
        state = MarkerState(
            position = LatLng(doorMarker.latitude, doorMarker.longitude),
        ),
        keys = arrayOf(doorMarker.id, scale),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (doorMarker.isWheelChairAccessible) MainGreen else Gray4,
                    shape = CircleShape,
                )
                .padding(horizontal = 4.dp * scale, vertical = 2.dp * scale)
                .sizeIn(minWidth = 16.dp * scale, minHeight = 16.dp * scale),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = doorMarker.label,
                style = KUBFAndroidTheme.typography.medium14.copy(
                    fontSize = fontSize.sp,
                    color = Color.White,
                ),
            )
        }
    }
}
