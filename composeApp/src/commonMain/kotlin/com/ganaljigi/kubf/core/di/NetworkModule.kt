package com.ganaljigi.kubf.core.di

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(): HttpClient
expect fun getBaseUrl(): String
expect fun isDebug(): Boolean

fun provideJson(): Json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = true
}

fun provideHttpClient(json: Json): HttpClient = createPlatformHttpClient().config {
    install(ContentNegotiation) {
        json(json)
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Napier.d(message, tag = "KtorClient")
            }
        }
        level = if (isDebug()) LogLevel.BODY else LogLevel.NONE
    }

    defaultRequest {
        url(getBaseUrl())
        contentType(ContentType.Application.Json)
    }
}
