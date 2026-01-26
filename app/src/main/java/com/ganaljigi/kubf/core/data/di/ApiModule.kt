package com.ganaljigi.kubf.core.data.di

import com.ganaljigi.kubf.core.network.service.BuildingService
import com.ganaljigi.kubf.core.network.service.HomeService
import com.ganaljigi.kubf.core.network.service.RouteService
import com.ganaljigi.kubf.core.network.service.HelperService
import com.ganaljigi.kubf.feature.room.service.RoomInfoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideBuildingService(retrofit: Retrofit): BuildingService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideRouteService(retrofit: Retrofit): RouteService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideHelperService(retrofit: Retrofit): HelperService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideRoomInfoService(retrofit: Retrofit): RoomInfoService {
        return retrofit.create()
    }
}
