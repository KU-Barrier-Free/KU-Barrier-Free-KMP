package com.ganaljigi.kubf.feature.room.di

import com.ganaljigi.kubf.feature.room.repository.RoomInfoRepository
import com.ganaljigi.kubf.feature.room.repositoryimpl.RoomInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomInfoRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRoomInfoRepository(
        impl: RoomInfoRepositoryImpl,
    ): RoomInfoRepository
}
