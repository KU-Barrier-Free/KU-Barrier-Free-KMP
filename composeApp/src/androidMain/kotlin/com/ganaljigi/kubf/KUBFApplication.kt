package com.ganaljigi.kubf

import android.app.Application
import com.ganalijigi.kubf.BuildConfig
import com.ganaljigi.kubf.core.di.appModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KUBFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initNapier()
        startKoin {
            androidLogger()
            androidContext(this@KUBFApplication)
            modules(appModules)
        }
    }

    private fun initNapier() {
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }
}
