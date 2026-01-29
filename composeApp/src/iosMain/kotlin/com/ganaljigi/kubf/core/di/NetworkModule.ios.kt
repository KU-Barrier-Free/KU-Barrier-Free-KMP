package com.ganaljigi.kubf.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSBundle

actual fun createPlatformHttpClient(): HttpClient = HttpClient(Darwin)

actual fun getBaseUrl(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("BASE_URL") as? String ?: ""
}

actual fun isDebug(): Boolean {
    return Platform.isDebugBinary
}
