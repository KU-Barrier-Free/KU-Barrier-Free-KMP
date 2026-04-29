package com.ganaljigi.kubf.core.config

import platform.Foundation.NSBundle

actual object AppConfig {
    actual val BASE_URL: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("BASE_URL") as? String ?: ""

    actual val GOOGLE_MAPS_API_KEY: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_MAPS_API_KEY") as? String ?: ""

    actual val GOOGLE_MAPS_ID: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_MAPS_ID") as? String ?: ""
}
