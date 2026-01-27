package com.ganaljigi.kubf.feature.home.component.map.marker

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.feature.home.component.map.infowindow.MarkerComposableInfoWindow
import com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
import com.google.maps.android.compose.MarkerState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SpecialMarkerComposable(
    markerState: MarkerState,
    isSelected: Boolean = false,
    specialMarkerInfo: SpecialMarkerInfo?,
    onSpecialMarkerClick: () -> Unit = {},
    onSpecialInfoClick: (List<String>) -> Unit = {},
    scale: Float = 1f,
) {
    val iconScale = if (isSelected) scale * 2.0f else scale
    val imageUrls = specialMarkerInfo?.imageUrls?.take(2) ?: emptyList()
    val isImageLoaded =
        remember(isSelected, imageUrls.size) { mutableStateListOf(*Array(imageUrls.size) { false }) }
    val painters = imageUrls.mapIndexed { index, imageUrl ->
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .allowHardware(false)
                .build(),
            placeholder = painterResource(R.drawable.img_special_info),
            error = painterResource(R.drawable.img_special_info),
            onSuccess = { isImageLoaded[index] = true },
        )
    }
    val allImagesLoaded by remember { derivedStateOf { isImageLoaded.all { it } } }

    LaunchedEffect(specialMarkerInfo, allImagesLoaded) {
        if (isSelected && allImagesLoaded) {
            markerState.showInfoWindow()
        } else if (!isSelected) {
            markerState.hideInfoWindow()
        }
    }

    MarkerComposableInfoWindow(
        markerState = markerState,
        isSelected = isSelected,
        specialMarkerInfo = SpecialMarkerInfo(
            imageUrls = specialMarkerInfo?.imageUrls ?: persistentListOf(),
            description = specialMarkerInfo?.description ?: "",
        ),
        onSpecialMarkerClick = onSpecialMarkerClick,
        onSpecialInfoClick = onSpecialInfoClick,
        painters = painters,
    ) {
        Icon(
            painter = painterResource(
                if (isSelected) R.drawable.ic_special_marker_selected
                else R.drawable.ic_special_marker,
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp * iconScale),
        )
    }
}
