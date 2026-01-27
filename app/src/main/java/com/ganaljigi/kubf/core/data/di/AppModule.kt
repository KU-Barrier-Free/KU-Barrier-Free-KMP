package com.ganaljigi.kubf.core.data.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NetworkModule::class, ApiModule::class])
@ComponentScan("com.ganaljigi.kubf")
class AppModule
