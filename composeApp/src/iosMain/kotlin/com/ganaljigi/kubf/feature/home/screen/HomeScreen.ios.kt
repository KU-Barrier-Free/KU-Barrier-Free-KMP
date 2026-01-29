package com.ganaljigi.kubf.feature.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ganaljigi.kubf.feature.home.viewmodel.HomeViewModel

@Composable
actual fun HomeRoute(
    padding: PaddingValues,
    navigateToHelper: () -> Unit,
    navigateToBuildingInfo: (Long) -> Unit,
    viewModel: HomeViewModel,
) {
    // TODO: iOS 지도 구현 필요
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Text("iOS Map - Coming Soon")
    }
}
