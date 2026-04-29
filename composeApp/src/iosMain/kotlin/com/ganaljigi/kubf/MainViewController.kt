package com.ganaljigi.kubf

import androidx.compose.ui.window.ComposeUIViewController
import com.ganaljigi.kubf.core.di.appModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

private var koinStarted = false

fun MainViewController() = ComposeUIViewController {
    // Koin 초기화 (한 번만 실행)
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }

    App()
}

private fun initKoin() {
    // Napier 초기화 (Debug 모드)
    Napier.base(DebugAntilog())

    // Koin 시작
    startKoin {
        modules(appModules)
    }
}
