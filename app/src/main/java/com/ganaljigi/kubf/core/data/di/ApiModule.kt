package com.ganaljigi.kubf.core.data.di

import com.ganaljigi.kubf.core.network.service.BuildingService
import com.ganaljigi.kubf.core.network.service.HelperService
import com.ganaljigi.kubf.core.network.service.HomeService
import com.ganaljigi.kubf.core.network.service.RouteService
import com.ganaljigi.kubf.feature.room.service.RoomInfoService
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.create

@Module
class ApiModule {
    @Single
    fun provideHomeService(retrofit: Retrofit): HomeService = retrofit.create()

    @Single
    fun provideBuildingService(retrofit: Retrofit): BuildingService = retrofit.create()

    @Single
    fun provideRouteService(retrofit: Retrofit): RouteService = retrofit.create()

    @Single
    fun provideHelperService(retrofit: Retrofit): HelperService = retrofit.create()

    @Single
    fun provideRoomInfoService(retrofit: Retrofit): RoomInfoService = retrofit.create()
}
