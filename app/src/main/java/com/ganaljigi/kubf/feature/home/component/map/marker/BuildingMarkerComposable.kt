package com.ganaljigi.kubf.feature.home.component.map.marker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.core.ui.util.noRippleClickable
import com.ganaljigi.kubf.feature.home.viewmodel.BuildingMarker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun BuildingMarkerComposable(
    buildingMarker: BuildingMarker,
    isSelected: Boolean = false,
    scale: Float = 1f,
    onClick: (BuildingMarker) -> Unit = {},
) {
    val markerState = rememberMarkerState(
        key = buildingMarker.id.toString(),
        position = LatLng(buildingMarker.latitude, buildingMarker.longitude),
    )
    val iconScale = if (isSelected) scale * 2.0f else scale
    val iconSize = if (isSelected) (32.dp * iconScale) else (20.dp * iconScale)
    val fontSize = (14 * scale).coerceAtLeast(10f)

    MarkerComposable(
        onClick = { onClick(buildingMarker); false },
        state = markerState,
        zIndex = if (isSelected) Float.MAX_VALUE else 0f,
        keys = arrayOf(buildingMarker.id, isSelected, scale),
    ) {
        Column(
            modifier = Modifier.noRippleClickable { onClick(buildingMarker) },
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(
                    if (isSelected) R.drawable.ic_building_selected
                    else R.drawable.ic_building,
                ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .shadow(10.dp)
                    .size(iconSize),
            )

            Box {
                Text(
                    text = buildingMarker.name,
                    style = KUBFAndroidTheme.typography.semiBold14.copy(
                        fontSize = fontSize.sp,
                        drawStyle = Stroke(width = 4f * scale),
                    ),
                    color = Color.White,
                )
                Text(
                    text = buildingMarker.name,
                    style = KUBFAndroidTheme.typography.semiBold14.copy(
                        fontSize = fontSize.sp,
                    ),
                    color = if (isSelected) MainGreen else Color(0xFF5A6860),
                )
            }
        }
    }
}
