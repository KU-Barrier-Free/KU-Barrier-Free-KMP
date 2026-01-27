package com.ganaljigi.kubf.feature.home.component.map.marker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.ganalijigi.kubf.R
import com.ganaljigi.kubf.feature.home.component.map.infowindow.GateMarkerInfoWindow
import com.ganaljigi.kubf.feature.home.viewmodel.GateMarkerInfo
import com.ganaljigi.kubf.feature.home.viewmodel.SpecialMarkerInfo
import com.google.maps.android.compose.MarkerState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GateMarkerComposable(
    markerState: MarkerState,
    isSelected: Boolean = false,
    gateMarkerInfo: GateMarkerInfo?,
    onGateMarkerClick: () -> Unit = {},
    onGateInfoClick: (List<String>) -> Unit = {},
    scale: Float = 1f,
) {
    val imageUrls = gateMarkerInfo?.imageUrls?.take(2) ?: emptyList()
    val isImageLoaded =
        remember(imageUrls, isSelected) { mutableStateListOf(*Array(imageUrls.size) { false }) }
    val painters = imageUrls.mapIndexed { index, imageUrl ->
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .allowHardware(false)
                .build(),
            placeholder = painterResource(R.drawable.img_special_info),
            error = painterResource(R.drawable.img_special_info),
            onSuccess = { isImageLoaded[index] = true },
            onError = { isImageLoaded[index] = true },
        )
    }
    val allImagesLoaded by remember { derivedStateOf { isImageLoaded.all { it } } }

    LaunchedEffect(gateMarkerInfo, allImagesLoaded) {
        if (isSelected && allImagesLoaded) {
            markerState.showInfoWindow()
        } else if (!isSelected) {
            markerState.hideInfoWindow()
        }
    }

    GateMarkerInfoWindow(
        markerState = markerState,
        isSelected = isSelected,
        gateMarkerInfo = SpecialMarkerInfo(
            imageUrls = gateMarkerInfo?.imageUrls ?: persistentListOf(),
            description = gateMarkerInfo?.description ?: "",
        ),
        onGateMarkerClick = onGateMarkerClick,
        onGateInfoClick = onGateInfoClick,
        painters = painters,
        scale = scale,
    )
}
