package com.ganaljigi.kubf.feature.home.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.ganaljigi.kubf.feature.home.viewmodel.HomeViewModel

@Composable
expect fun HomeRoute(
    padding: PaddingValues,
    navigateToHelper: () -> Unit = {},
    navigateToBuildingInfo: (Long) -> Unit = {},
    viewModel: HomeViewModel,
)
