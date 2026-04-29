package com.ganaljigi.kubf.feature.home.component.map.infowindow

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
import com.ganaljigi.kubf.core.ui.util.noRippleClickable
import com.ganaljigi.kubf.feature.home.component.map.rememberComposeBitmapDescriptor
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState

@Composable
fun GateMarkerInfoWindow(
    markerState: MarkerState,
    isSelected: Boolean,
    gateMarkerInfo: SpecialMarkerInfo,
    onGateMarkerClick: () -> Unit = { },
    onGateInfoClick: (List<String>) -> Unit = { },
    painters: List<Painter> = emptyList(),
    scale: Float = 1f,
) {
    val icon = rememberComposeBitmapDescriptor(isSelected) {
        Icon(
            painter = painterResource(R.drawable.ic_gate_pin),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(30.dp * scale),
        )
    }

    MarkerInfoWindow(
        state = markerState,
        onClick = {
            onGateMarkerClick()
            false
        },
        onInfoWindowClick = {
            onGateInfoClick(gateMarkerInfo.imageUrls)
        },
        icon = icon,
        infoWindowAnchor = Offset(0.5f, -0.2f),
    ) {
        MapGateInfo(
            painters = painters,
            description = gateMarkerInfo.description,
            modifier = Modifier.noRippleClickable {
                onGateInfoClick(gateMarkerInfo.imageUrls)
            },
        )
    }
}
