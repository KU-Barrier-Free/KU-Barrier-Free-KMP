package com.ganaljigi.kubf

import android.app.Application
import com.ganaljigi.kubf.core.data.di.ApiModule
import com.ganaljigi.kubf.core.data.di.NetworkModule
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
}
