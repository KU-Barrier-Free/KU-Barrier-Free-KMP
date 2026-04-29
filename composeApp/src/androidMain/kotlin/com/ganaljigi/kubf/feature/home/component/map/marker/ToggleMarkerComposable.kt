package com.ganaljigi.kubf.feature.home.component.map.marker

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ganaljigi.kubf.feature.home.viewmodel.ToggleMarker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun ToggleMarkerComposable(
    toggleMarker: ToggleMarker,
    @DrawableRes toggleIconRes: Int,
    scale: Float = 1f,
) {
    val markerState = rememberMarkerState(
        key = toggleMarker.toString(),
        position = LatLng(toggleMarker.latitude, toggleMarker.longitude),
    )
    key(toggleMarker) {
        MarkerComposable(
            state = markerState,
        ) {
            Icon(
                painter = painterResource(toggleIconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(16.dp * scale)
                    .shadow(1.dp),
            )
        }
    }
    DisposableEffect(toggleMarker) {
        onDispose { markerState.position }
    }
}
