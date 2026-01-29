package com.ganaljigi.kubf.core.di

import com.ganaljigi.kubf.core.data.repository.BuildingInfoRepository
import com.ganaljigi.kubf.core.data.repository.BuildingRepository
import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.data.repository.InquiryRepository
import com.ganaljigi.kubf.core.data.repository.RoomInfoRepository
import com.ganaljigi.kubf.core.data.repository.RouteRepository
import com.ganaljigi.kubf.core.data.repositoryimpl.BuildingInfoRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.BuildingRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.HomeRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.InquiryRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.RoomInfoRepositoryImpl
import com.ganaljigi.kubf.core.data.repositoryimpl.RouteRepositoryImpl
import com.ganaljigi.kubf.core.network.api.BuildingApi
import com.ganaljigi.kubf.core.network.api.HelperApi
import com.ganaljigi.kubf.core.network.api.HomeApi
import com.ganaljigi.kubf.core.network.api.InquiryApi
import com.ganaljigi.kubf.core.network.api.RoomInfoApi
import com.ganaljigi.kubf.core.network.api.RouteApi
import com.ganaljigi.kubf.feature.building.viewmodel.BuildingViewModel
import com.ganaljigi.kubf.feature.helper.repository.HelperRepository
import com.ganaljigi.kubf.feature.helper.repositoryimpl.HelperRepositoryImpl
import com.ganaljigi.kubf.feature.helper.viewmodel.HelperViewModel
import com.ganaljigi.kubf.feature.home.viewmodel.HomeViewModel
import com.ganaljigi.kubf.feature.room.viewmodel.RoomInfoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { provideJson() }
    single { provideHttpClient(get()) }
}

val apiModule = module {
    single { HomeApi(get()) }
    single { BuildingApi(get()) }
    single { RouteApi(get()) }
    single { HelperApi(get()) }
    single { RoomInfoApi(get()) }
    single { InquiryApi(get()) }
}

val repositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<BuildingRepository> { BuildingRepositoryImpl(get()) }
    single<BuildingInfoRepository> { BuildingInfoRepositoryImpl(get()) }
    single<RouteRepository> { RouteRepositoryImpl(get()) }
    single<HelperRepository> { HelperRepositoryImpl(get()) }
    single<RoomInfoRepository> { RoomInfoRepositoryImpl(get()) }
    single<InquiryRepository> { InquiryRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { (savedStateHandle: androidx.lifecycle.SavedStateHandle) ->
        BuildingViewModel(savedStateHandle, get())
    }
    viewModel { HelperViewModel(get()) }
    viewModel { (savedStateHandle: androidx.lifecycle.SavedStateHandle) ->
        RoomInfoViewModel(savedStateHandle, get())
    }
}

val appModules = listOf(
    networkModule,
    apiModule,
    repositoryModule,
    viewModelModule,
)
