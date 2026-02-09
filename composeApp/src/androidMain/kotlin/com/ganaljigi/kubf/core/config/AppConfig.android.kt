package com.ganaljigi.kubf.core.config

import com.ganalijigi.kubf.BuildConfig

actual object AppConfig {
    actual val BASE_URL: String = BuildConfig.BASE_URL
    actual val GOOGLE_MAPS_API_KEY: String = ""  // AndroidManifest에서 사용
    actual val GOOGLE_MAPS_ID: String = BuildConfig.GOOGLE_MAPS_ID
}
