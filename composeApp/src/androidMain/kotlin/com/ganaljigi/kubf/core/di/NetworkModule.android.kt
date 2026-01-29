package com.ganaljigi.kubf.core.di

import com.ganalijigi.kubf.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createPlatformHttpClient(): HttpClient = HttpClient(Android)

actual fun getBaseUrl(): String = BuildConfig.BASE_URL

actual fun isDebug(): Boolean = BuildConfig.DEBUG
