package com.ganaljigi.kubf.core.data.di

import com.ganaljigi.kubf.core.data.repository.BuildingRepository
import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.data.repository.RouteRepository
import com.ganaljigi.kubf.core.data.repositoryimpl.BuildingRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.HomeRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.RouteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindBuildingRepository(buildingRepositoryImpl: BuildingRepositoryImpl): BuildingRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(routeRepositoryImpl: RouteRepositoryImpl): RouteRepository
}
