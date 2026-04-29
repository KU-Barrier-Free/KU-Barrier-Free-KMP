package com.ganaljigi.kubf.core.di

import com.ganaljigi.kubf.core.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlin.experimental.ExperimentalNativeApi

actual fun createPlatformHttpClient(): HttpClient = HttpClient(Darwin)

actual fun getBaseUrl(): String = AppConfig.BASE_URL

@OptIn(ExperimentalNativeApi::class)
actual fun isDebug(): Boolean = Platform.isDebugBinary
