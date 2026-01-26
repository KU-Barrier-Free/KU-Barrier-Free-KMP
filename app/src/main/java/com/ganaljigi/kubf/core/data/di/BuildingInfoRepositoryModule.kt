package com.ganaljigi.kubf.core.data.di

import com.ganaljigi.kubf.core.data.repository.BuildingInfoRepository
import com.ganaljigi.kubf.core.data.repositoryimpl.BuildingInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BuildingInfoRepositoryModule{
    @Binds
    @Singleton
    abstract fun bindBuildingInfoRepository(
        impl: BuildingInfoRepositoryImpl
    ):BuildingInfoRepository
}