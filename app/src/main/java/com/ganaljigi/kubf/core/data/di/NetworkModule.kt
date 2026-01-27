package com.ganaljigi.kubf.core.data.di

import android.util.Log
import com.ganalijigi.kubf.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NetworkModule {
    @Single
    fun providesJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    @Single
    fun providesHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KtorClient", message)
                }
            }
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }

        defaultRequest {
            url(BuildConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}
