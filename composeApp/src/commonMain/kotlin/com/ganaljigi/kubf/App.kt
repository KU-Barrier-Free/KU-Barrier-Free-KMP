package com.ganaljigi.kubf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ganaljigi.kubf.core.designsystem.theme.KUBFAndroidTheme
import com.ganaljigi.kubf.core.navigation.MainNavHost

@Composable
fun App() {
    KUBFAndroidTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            MainNavHost(
                padding = innerPadding,
                navController = navController,
            )
        }
    }
}
