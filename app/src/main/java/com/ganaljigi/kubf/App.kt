package com.ganaljigi.kubf

import android.app.Application
import com.ganalijigi.kubf.BuildConfig
import com.ganaljigi.kubf.core.data.di.ApiModule
import com.ganaljigi.kubf.core.data.di.NetworkModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

@KoinApplication
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initNapier()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                defaultModule,
                ApiModule().module,
                NetworkModule().module,
            )
        }
    }

    private fun initNapier() {
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }
}
