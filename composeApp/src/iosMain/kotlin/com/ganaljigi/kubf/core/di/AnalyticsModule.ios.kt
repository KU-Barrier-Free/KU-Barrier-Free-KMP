package com.ganaljigi.kubf.core.di

import com.ganaljigi.kubf.core.analytics.AnalyticsLogger
import com.ganaljigi.kubf.core.analytics.AnalyticsTracker
import com.ganaljigi.kubf.core.analytics.FirebaseAnalyticsLogger
import org.koin.core.module.Module
import org.koin.dsl.module

actual val analyticsModule: Module = module {
    single<AnalyticsLogger> { FirebaseAnalyticsLogger() }
    single { AnalyticsTracker(get()) }
}
