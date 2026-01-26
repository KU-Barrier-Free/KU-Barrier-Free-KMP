package com.ganaljigi.kubf.feature.helper.di

import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import com.ganaljigi.kubf.feature.helper.repositoryimpl.HelperRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HelperRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHelperRepository(
        impl: HelperRepositoryImpl,
    ): HelperRepository
}
