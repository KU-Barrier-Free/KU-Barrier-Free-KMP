package com.ganaljigi.kubf.core.analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNumber

@OptIn(ExperimentalForeignApi::class)
class FirebaseAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, Any>) {
        FIRAnalytics.logEventWithName(name, params.toFirebaseParams())
    }

    private fun Map<String, Any>.toFirebaseParams(): Map<Any?, *> =
        mapKeys { it.key }.mapValues { (_, value) ->
            when (value) {
                is Boolean -> value.toString()
                is Int -> NSNumber(int = value)
                is Long -> NSNumber(longLong = value)
                is Float -> NSNumber(float = value)
                is Double -> NSNumber(double = value)
                is String -> value
                else -> value.toString()
            }
        }
}
